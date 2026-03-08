package com.example.account.modules.facturation.service.ExternalServices.entity.WebsocketUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.account.modules.facturation.dto.response.ExternalResponses.ProductResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class StockBroadcaster {
    // This is your "Topic". It holds a stream of updates.
    private final Map<String, Sinks.Many<ProductResponse>> orgSinks = new ConcurrentHashMap<>();

    public void broadcast(ProductResponse product) {
        String orgId = product.getOrganizationId().toString();
        
        // Get the existing sink for this org, or create a new one if it doesn't exist
        Sinks.Many<ProductResponse> sink = orgSinks.computeIfAbsent(orgId, 
            key -> Sinks.many().multicast().directBestEffort());
            
        sink.tryEmitNext(product);
    }

    public Flux<ProductResponse> getMessagesForOrg(String orgId) {
        return orgSinks.computeIfAbsent(orgId, 
            key -> Sinks.many().multicast().directBestEffort())
            .asFlux();
    }
}