package com.example.account.modules.tiers.adapter.output.persistence;

import com.example.account.modules.tiers.domain.model.Fournisseur;
import com.example.account.modules.tiers.domain.model.enums.TypeClient;
import com.example.account.modules.tiers.domain.port.output.FournisseurRepositoryPort;
import com.example.account.modules.tiers.adapter.output.persistence.mapper.FournisseurPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FournisseurPersistenceAdapter implements FournisseurRepositoryPort {

    private final FournisseurR2dbcRepository repository;
    private final FournisseurPersistenceMapper mapper;

    @Override
    public Mono<Fournisseur> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Mono<Fournisseur> findByUsername(String username) {
        return repository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Mono<Fournisseur> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Mono<Fournisseur> findByCodeFournisseur(String codeFournisseur) {
        return repository.findByCodeFournisseur(codeFournisseur).map(mapper::toDomain);
    }

    @Override
    public Flux<Fournisseur> findByTypeFournisseur(TypeClient typeFournisseur) {
        return repository.findByTypeFournisseur(typeFournisseur).map(mapper::toDomain);
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
    public Flux<Fournisseur> findAllActiveFournisseurs() {
        return repository.findAllActiveFournisseurs().map(mapper::toDomain);
    }

    @Override
    public Mono<Long> countActiveFournisseurs() {
        return repository.countActiveFournisseurs();
    }

    @Override
    public Mono<Fournisseur> save(Fournisseur fournisseur) {
        return repository.save(mapper.toEntity(fournisseur)).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        return repository.existsById(id);
    }
}
