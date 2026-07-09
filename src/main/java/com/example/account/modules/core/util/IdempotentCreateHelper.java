package com.example.account.modules.core.util;

import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public final class IdempotentCreateHelper {

    private IdempotentCreateHelper() {
    }

    public static <T, R> Mono<R> createOrReturnExisting(
            UUID id,
            Function<UUID, Mono<T>> findById,
            Function<T, R> toResponse,
            Supplier<Mono<R>> createNew) {
        if (id == null) {
            return createNew.get();
        }
        return findById.apply(id)
                .map(existing -> {
                    return toResponse.apply(existing);
                })
                .switchIfEmpty(Mono.defer(createNew));
    }
}
