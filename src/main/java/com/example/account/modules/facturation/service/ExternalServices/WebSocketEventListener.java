package com.example.account.modules.facturation.service.ExternalServices;



import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        log.info("=== NEW SUBSCRIPTION ===");
        log.info("Session ID: {}", sessionId);
        log.info("Subscribed to: {}", destination);
        
        // You can also check for user info if you have security enabled
        if (headerAccessor.getUser() != null) {
            log.info("User: {}", headerAccessor.getUser().getName());
        }
    }
}