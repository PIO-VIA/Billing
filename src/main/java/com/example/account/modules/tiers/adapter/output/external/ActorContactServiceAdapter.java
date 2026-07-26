package com.example.account.modules.tiers.adapter.output.external;

import com.example.account.modules.shared.dto.kernel.KernelActorContactResponse;
import com.example.account.modules.shared.dto.kernel.KernelApiResponse;
import com.example.account.modules.tiers.domain.port.output.ActorContactServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ActorContactServiceAdapter implements ActorContactServicePort {

    private final WebClient kernelWebClient;

    private static final ParameterizedTypeReference<KernelApiResponse<List<KernelActorContactResponse>>> CONTACT_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    public ActorContactServiceAdapter(@Qualifier("kernelWebClient") WebClient kernelWebClient) {
        this.kernelWebClient = kernelWebClient;
    }

    @Override
    public Mono<String> getPrimaryEmail(UUID actorId) {
        return kernelWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/contacts")
                        .queryParam("contactableType", "ACTOR")
                        .queryParam("contactableId", actorId)
                        .build())
                .retrieve()
                .bodyToMono(CONTACT_LIST_TYPE)
                .map(KernelApiResponse::getData)
                .flatMapMany(Flux::fromIterable)
                .filter(c -> c.getEmail() != null && !c.getEmail().isBlank())
                .sort(Comparator.comparing(KernelActorContactResponse::isFavorite).reversed())
                .next()
                .map(KernelActorContactResponse::getEmail)
                .defaultIfEmpty("")
                .onErrorReturn("");
    }
}
