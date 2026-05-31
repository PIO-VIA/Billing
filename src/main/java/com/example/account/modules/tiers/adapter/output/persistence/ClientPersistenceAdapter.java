package com.example.account.modules.tiers.adapter.output.persistence;

import com.example.account.modules.tiers.domain.model.Client;
import com.example.account.modules.tiers.domain.model.enums.TypeClient;
import com.example.account.modules.tiers.domain.port.output.ClientRepositoryPort;
import com.example.account.modules.tiers.adapter.output.persistence.mapper.ClientPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClientPersistenceAdapter implements ClientRepositoryPort {

    private final ClientR2dbcRepository repository;
    private final ClientPersistenceMapper mapper;

    @Override
    public Mono<Client> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Mono<Client> findByUsername(String username) {
        return repository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Mono<Client> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Mono<Client> findByCodeClient(String codeClient) {
        return repository.findByCodeClient(codeClient).map(mapper::toDomain);
    }

    @Override
    public Flux<Client> findByTypeClient(TypeClient typeClient) {
        return repository.findByTypeClient(typeClient).map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Flux<Client> findAllActiveClients() {
        return repository.findAllActiveClients().map(mapper::toDomain);
    }

    @Override
    public Mono<Long> countActiveClients() {
        return repository.countActiveClients();
    }

    @Override
    public Mono<Client> save(Client client) {
        return repository.save(mapper.toEntity(client)).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Mono<Long> count() {
        return repository.count();
    }
}
