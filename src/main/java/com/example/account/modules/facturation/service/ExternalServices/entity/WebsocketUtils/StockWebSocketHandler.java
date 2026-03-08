package com.example.account.modules.facturation.service.ExternalServices.entity.WebsocketUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.account.modules.facturation.dto.request.ExternalRequest.ReserveRequest;
import com.example.account.modules.facturation.service.ExternalServices.ProductExternalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
@Slf4j // Using Slf4j for better logging than System.out
public class StockWebSocketHandler implements WebSocketHandler {

    private final StockBroadcaster broadcaster;
    private final ObjectMapper objectMapper;
    private final ProductExternalService productService; // Inject your service here later
    // private final ProductService productService; // Inject your service here later
    private final Map<UUID, Sinks.Many<String>> sellerSinks = new ConcurrentHashMap<>();


  @Override
public Mono<Void> handle(WebSocketSession session) {
    URI uri = session.getHandshakeInfo().getUri();
   // Build the query map once
    var queryParams = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
    
    String orgId = queryParams.getFirst("orgId");
    String sellerId = queryParams.getFirst("sellerId");
    UUID sellerUUID = UUID.fromString(sellerId);

    if (orgId == null) return session.close(CloseStatus.BAD_DATA);

    // 1. Create a "Private Channel" for this specific user (for errors or direct replies)
    Sinks.Many<String> privateSink = Sinks.many().unicast().onBackpressureBuffer();
    //add seller to map
    sellerSinks.put(sellerUUID, privateSink);
    // --- PART A: RECEIVE ---
    Mono<Void> inbound = session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .flatMap(payload -> {
                try {
                    ReserveRequest request = objectMapper.readValue(payload, ReserveRequest.class);
                    Mono<Void> actionMono;
                    //canceling reservatoin
        if (ReserveRequest.ActionType.CANCEL.equals(request.getAction())) {
            log.info("Canceling reservation for product: {}", request.getProductId());
            actionMono = productService.releaseProduct(request.getProductId(), request.getQuantity());
        } else {//makiing a reservation
            log.info("Making reservation for product: {}", request.getProductId());
            actionMono = productService.reserveProduct(request);
        }

        return actionMono
            .onErrorResume(e -> {
                log.warn("Business Error: {}", e.getMessage());
                String errorJson = String.format("{\"type\": \"ERROR\", \"message\": \"%s\"}", e.getMessage());
                privateSink.tryEmitNext(errorJson);
                return Mono.empty(); 
            });


                } catch (Exception e) {
                    return Mono.empty();
                }
            }).then();

    // --- PART B: MERGED OUTBOUND ---
    // Combine the Broadcaster updates AND the private messages (errors)
    Flux<WebSocketMessage> outbound = Flux.merge(
                broadcaster.getMessagesForOrg(orgId).map(p -> serialize(p)), // Public updates
                privateSink.asFlux() // Private errors
            )
            .map(session::textMessage);
// --- PART C: CLEANUP ---
    return Mono.when(inbound, session.send(outbound))
            .doFinally(signal -> {
                // IMPORTANT: Remove the sink when seller disconnects
                sellerSinks.remove(sellerId);
                log.info("Seller {} session cleared from map.", sellerId);
            });
}

// Helper to keep code clean
private String serialize(Object obj) {
    try { return objectMapper.writeValueAsString(obj); } 
    catch (Exception e) { return "{}"; }
}

public void notifySeller(UUID sellerId, String message) {
    Sinks.Many<String> sink = sellerSinks.get(sellerId);
    if (sink != null) {
        sink.tryEmitNext(message);
    }}
}