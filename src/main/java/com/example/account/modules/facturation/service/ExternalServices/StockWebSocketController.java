package com.example.account.modules.facturation.service.ExternalServices;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.account.modules.facturation.dto.request.ExternalRequest.ReserveRequest;
import com.example.account.modules.facturation.dto.response.ExternalResponses.ProductResponse;

import reactor.core.publisher.Mono;

@Controller
public class StockWebSocketController {

    private final RedisService redisService; // your service for stock/reservations

    public StockWebSocketController(RedisService redisService) {
        this.redisService = redisService;
    }

    // Client sends a message to /app/reserve
    @MessageMapping("/reserve")
    @SendTo("/topic/stock")
    public void reserve(ReserveRequest request) {

        System.out.println("Seller connected");
        System.out.println(request);
        //now redis service creates a reservation 
        // 1. Update Redis stock atomically
        //int newAvailable = redisService.reserveProduct(request.getProductId(), request.getQty(), request.getUserId());

        // 2. Return update to broadcast to all subscribers
       // return Mono.just(new StockUpdate(request.getProductId(), newAvailable));
    }
}
