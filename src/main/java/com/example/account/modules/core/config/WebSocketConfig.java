package com.example.account.modules.core.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import com.example.account.modules.facturation.service.ExternalServices.entity.WebsocketUtils.StockWebSocketHandler;

@Configuration
public class WebSocketConfig {

    @Bean
    public HandlerMapping webSocketHandlerMapping(StockWebSocketHandler handler) {
        // This maps the URL 'ws://localhost:8080/ws-stock'
        Map<String, WebSocketHandler> map = Map.of("/ws-stock", handler);
        return new SimpleUrlHandlerMapping(map, 1);
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}