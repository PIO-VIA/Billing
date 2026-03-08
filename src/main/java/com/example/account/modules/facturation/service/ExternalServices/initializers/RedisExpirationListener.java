package com.example.account.modules.facturation.service.ExternalServices.initializers;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import com.example.account.modules.facturation.repository.ProductRepository;
import com.example.account.modules.facturation.service.ExternalServices.ProductExternalService;
import com.example.account.modules.facturation.service.ExternalServices.entity.WebsocketUtils.StockBroadcaster;
import com.example.account.modules.facturation.service.ExternalServices.entity.WebsocketUtils.StockWebSocketHandler;
import com.example.account.modules.facturation.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

@Component
@Slf4j
public class RedisExpirationListener extends KeyExpirationEventMessageListener {

    private final ProductRepository productRepository;
    private final StockBroadcaster broadcaster;
    private final ProductMapper productMapper;
    private final ProductExternalService productExternalService;
    private final StockWebSocketHandler stockWebSocketHandler;
    public RedisExpirationListener(RedisMessageListenerContainer listenerContainer, 
                                   ProductRepository productRepository,
                                   StockBroadcaster broadcaster,
                                   ProductMapper productMapper,ProductExternalService productExternalService,
                                   StockWebSocketHandler stockWebSocketHandler
                                ) {
        super(listenerContainer);
        this.productRepository = productRepository;
        this.broadcaster = broadcaster;
        this.productMapper = productMapper;
        this.productExternalService=productExternalService;
        this.stockWebSocketHandler=stockWebSocketHandler;
    }

@Override
public void onMessage(Message message, byte[] pattern) {
    String expiredKey = message.toString(); 
    
    // Safety check: ensure it's one of our reservation keys
    if (expiredKey != null && expiredKey.startsWith("res:")) {
        try {
            log.info("⏰ Reservation expired! Key: {}", expiredKey);
            
            // Expected format from your reserveProduct: res:sellerId:productId:qty
            String[] parts = expiredKey.split(":");
            
            // Extracting values based on the ':' delimiter
            UUID sellerId = UUID.fromString(parts[1]);
            UUID productId = UUID.fromString(parts[2]);
            Integer quantity = Integer.parseInt(parts[3]);

            // 1. Database & Broadcast Logic
            // We subscribe because this is a background task (no controller to handle the Mono)
            productExternalService.releaseProduct(productId, quantity)
                .subscribe(
                    v -> log.info("✅ Stock successfully restored for product: {}", productId),
                    err -> log.error("❌ Failed to release stock for product {}: {}", productId, err.getMessage())
                );

            // 2. Notify the Specific Seller
            // It's helpful to include the productId so the frontend knows WHICH item expired
            String expiredNotification = String.format(
                "{\"type\": \"EXPIRED\", \"productId\": \"%s\", \"message\": \"Reservation for %d units timed out.\"}",
                productId, quantity
            );
            
            stockWebSocketHandler.notifySeller(sellerId, expiredNotification);
            
        } catch (Exception e) {
            log.error("❌ Error parsing expired key {}: {}. Ensure format is res:sellerId:productId:qty", 
                      expiredKey, e.getMessage());
        }
    }
}
    
}