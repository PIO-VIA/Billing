package com.example.account.modules.tiers.service;

import com.example.account.modules.tiers.dto.ClientCreateRequest;
import com.example.account.modules.tiers.dto.ClientUpdateRequest;
import com.example.account.modules.tiers.dto.ClientResponse;
import com.example.account.modules.tiers.mapper.ClientMapper;
import com.example.account.modules.tiers.model.entity.Client;
import com.example.account.modules.tiers.model.enums.TypeClient;
import com.example.account.modules.tiers.repository.ClientRepository;
import com.example.account.modules.tiers.service.producer.ClientEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {
    
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientEventProducer clientEventProducer;

    @Transactional
    public Mono<ClientResponse> createClient(ClientCreateRequest request) {
        log.info("Création d'un nouveau client: {}", request.getUsername());

        return clientRepository.existsByUsername(request.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Un client avec ce username existe déjà"));
                    }
                    return request.getEmail() != null ? clientRepository.existsByEmail(request.getEmail()) : Mono.just(false);
                })
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new IllegalArgumentException("Un client avec cet email existe déjà"));
                    }
                    
                    Client client = clientMapper.toEntity(request);
                    if (client.getIdClient() == null) {
                        client.setIdClient(UUID.randomUUID());
                    }
                    return clientRepository.save(client);
                })
                .map(savedClient -> {
                    ClientResponse response = clientMapper.toResponse(savedClient);
                    clientEventProducer.publishClientCreated(response);
                    log.info("Client créé avec succès: {}", savedClient.getIdClient());
                    return response;
                });
    }

    @Transactional
    public Mono<ClientResponse> updateClient(UUID clientId, ClientUpdateRequest request) {
        log.info("Mise à jour du client: {}", clientId);

        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Client non trouvé: " + clientId)))
                .flatMap(client -> {
                    clientMapper.updateEntityFromRequest(request, client);
                    return clientRepository.save(client);
                })
                .map(updatedClient -> {
                    ClientResponse response = clientMapper.toResponse(updatedClient);
                    clientEventProducer.publishClientUpdated(response);
                    log.info("Client mis à jour avec succès: {}", clientId);
                    return response;
                });
    }

    @Transactional(readOnly = true)
    public Mono<ClientResponse> getClientById(UUID clientId) {
        log.info("Récupération du client: {}", clientId);

        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Client non trouvé: " + clientId)))
                .map(clientMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<ClientResponse> getClientByUsername(String username) {
        log.info("Récupération du client par username: {}", username);

        return clientRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Client non trouvé avec username: " + username)))
                .map(clientMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<ClientResponse> getAllClients() {
        log.info("Récupération de tous les clients");
        return clientRepository.findAll()
                .map(clientMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<ClientResponse> getActiveClients() {
        log.info("Récupération des clients actifs");
        return clientRepository.findAllActiveClients()
                .map(clientMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<ClientResponse> getClientsByType(TypeClient typeClient) {
        log.info("Récupération des clients par type: {}", typeClient);
        return clientRepository.findByTypeClient(typeClient)
                .map(clientMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteClient(UUID clientId) {
        log.info("Suppression du client: {}", clientId);

        return clientRepository.existsById(clientId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Client non trouvé: " + clientId));
                    }
                    return clientRepository.deleteById(clientId)
                            .then(Mono.fromRunnable(() -> clientEventProducer.publishClientDeleted(clientId)));
                })
                .then()
                .doOnSuccess(v -> log.info("Client supprimé avec succès: {}", clientId));
    }

    @Transactional
    public Mono<ClientResponse> updateSolde(UUID clientId, Double montant) {
        log.info("Mise à jour du solde du client {}: {}", clientId, montant);

        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Client non trouvé: " + clientId)))
                .flatMap(client -> {
                    client.setSoldeCourant(client.getSoldeCourant() + montant);
                    return clientRepository.save(client);
                })
                .map(clientMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countActiveClients() {
        return clientRepository.countActiveClients();
    }
}
