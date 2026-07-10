package com.example.account.modules.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Reachability check for the frontend's offline-mode connectivity probe
 * (src/offline/network/connectivity.ts pings this, not /actuator/health,
 * since actuator is often locked down differently per environment).
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Backend reachability check for the frontend's offline-mode detection")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check that the backend is reachable")
    public Mono<Map<String, String>> health() {
        return Mono.just(Map.of("status", "UP"));
    }
}
