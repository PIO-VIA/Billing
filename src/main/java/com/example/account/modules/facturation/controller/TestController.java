package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.service.ExternalServices.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final SellerService sellerService;

    @GetMapping("/test/{Id}")
    public Mono<String> getMethodName(@RequestParam UUID param) {
        return sellerService.getSellersByOrganization(param)
                .then(Mono.just("done"));
    }
}
