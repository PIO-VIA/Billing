package com.example.account.service;

import com.example.account.dto.request.ClientCreateRequest;
import com.example.account.dto.request.ClientUpdateRequest;
import com.example.account.dto.response.ClientResponse;
import com.example.account.mapper.ClientMapper;
import com.example.account.model.entity.Client;
import com.example.account.model.enums.TypeClient;
import com.example.account.repository.ClientRepository;
import com.example.account.service.producer.ClientEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientEventProducer clientEventProducer;



    @Transactional
    public ClientResponse createClient(ClientCreateRequest request) {
        log.info("Création d'un nouveau client: {}", request.getUsername());

        // Vérifications
        if (clientRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Un client avec ce username existe déjà");
        }
        if (request.getEmail() != null && clientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un client avec cet email existe déjà");
        }

        // Créer et sauvegarder le client
        Client client = clientMapper.toEntity(request);
        Client savedClient = clientRepository.save(client);
        ClientResponse response = clientMapper.toResponse(savedClient);

        // Publier l'événement
        clientEventProducer.publishClientCreated(response);

        log.info("Client créé avec succès: {}", savedClient.getIdClient());
        return response;
    }

    @Transactional
    public ClientResponse updateClient(UUID clientId, ClientUpdateRequest request) {
        log.info("Mise à jour du client: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé: " + clientId));

        // Mise à jour
        clientMapper.updateEntityFromRequest(request, client);
        Client updatedClient = clientRepository.save(client);
        ClientResponse response = clientMapper.toResponse(updatedClient);

        // Publier l'événement
        clientEventProducer.publishClientUpdated(response);

        log.info("Client mis à jour avec succès: {}", clientId);
        return response;
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(UUID clientId) {
        log.info("Récupération du client: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé: " + clientId));

        return clientMapper.toResponse(client);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientByUsername(String username) {
        log.info("Récupération du client par username: {}", username);

        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé avec username: " + username));

        return clientMapper.toResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        log.info("Récupération de tous les clients");
        List<Client> clients = clientRepository.findAll();
        return clientMapper.toResponseList(clients);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getActiveClients() {
        log.info("Récupération des clients actifs");
        List<Client> clients = clientRepository.findAllActiveClients();
        return clientMapper.toResponseList(clients);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getClientsByType(TypeClient typeClient) {
        log.info("Récupération des clients par type: {}", typeClient);
        List<Client> clients = clientRepository.findByTypeClient(typeClient);
        return clientMapper.toResponseList(clients);
    }

    @Transactional
    public void deleteClient(UUID clientId) {
        log.info("Suppression du client: {}", clientId);

        if (!clientRepository.existsById(clientId)) {
            throw new IllegalArgumentException("Client non trouvé: " + clientId);
        }

        clientRepository.deleteById(clientId);

        // Publier l'événement
        clientEventProducer.publishClientDeleted(clientId);

        log.info("Client supprimé avec succès: {}", clientId);
    }

    @Transactional
    public ClientResponse updateSolde(UUID clientId, Double montant) {
        log.info("Mise à jour du solde du client {}: {}", clientId, montant);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé: " + clientId));

        client.setSoldeCourant(client.getSoldeCourant() + montant);
        Client updatedClient = clientRepository.save(client);

        return clientMapper.toResponse(updatedClient);
    }

    @Transactional(readOnly = true)
    public Long countActiveClients() {
        return clientRepository.countActiveClients();
    }
}
