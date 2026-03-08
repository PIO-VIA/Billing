package com.example.account.modules.core.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    @Primary
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {

        RedisSerializationContext<String, String> context =
                RedisSerializationContext
                        .<String, String>newSerializationContext(new StringRedisSerializer())
                        .value(new StringRedisSerializer())
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    /**
     * This piece of code runs on startup and tells Redis to send events 
     * whenever a key expires (Ex). Without this, your listener will stay silent.
     */
    @Bean
    public ApplicationRunner configureRedisNotifications(RedisConnectionFactory connectionFactory) {
        return args -> {
            var connection = connectionFactory.getConnection();
            connection.setConfig("notify-keyspace-events", "Ex");
            System.out.println("✅ Redis Keyspace Notifications (Expiration) have been enabled.");
        };
    }
}