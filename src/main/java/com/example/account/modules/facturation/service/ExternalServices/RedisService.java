package com.example.account.modules.facturation.service.ExternalServices;

import java.time.Duration;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class RedisService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public RedisService(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> save(String key, String value) {
        return redisTemplate.opsForValue().set(key, value);
    }
     public Mono<Boolean> save(String key, String value, Integer ttlMinutes) {
        return redisTemplate.opsForValue().set(key, value,Duration.ofMinutes(ttlMinutes));
    }

    public Mono<String> get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Flux<String> getKeysByPrefix(String prefix) {
    // 1. Define the pattern (e.g., "res:*")
    ScanOptions options = ScanOptions.scanOptions()
            .match(prefix + "*")
            .count(100) // How many keys to fetch per "batch"
            .build();

    // 2. Use the scan method to return a Flux
    return redisTemplate.scan(options)
            .doOnNext(key ->System.out.println("Found active reservation key: "+ key));
    }
  public Flux<String> getKeys(String pattern) {
    // Pattern should be something like "res:*" to find all reservations
    // or "*" to find everything.
    return redisTemplate.keys(pattern);
}

public Mono<Void> deleteKey(String key) {
    // opsForValue().delete() returns Mono<Boolean> or Mono<Long> 
    // depending on your template version. 
    // We use .then() to turn it into Mono<Void>.
    return redisTemplate.delete(key)
            .doOnNext(deleted -> log.info("Deleted Redis key: {} -> {}", key, deleted))
            .then();
}
}

