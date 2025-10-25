package com.example.account.service;

import com.example.account.dto.request.BanqueCreateRequest;
import com.example.account.dto.request.BanqueUpdateRequest;
import com.example.account.dto.response.BanqueResponse;
import com.example.account.mapper.BanqueMapper;
import com.example.account.model.entity.Banque;
import com.example.account.repository.BanqueRepository;
import com.example.account.service.producer.BanqueEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BanqueService {

    private final BanqueRepository banqueRepository;
    private final BanqueEventProducer banqueEventProducer;
    private final BanqueMapper banqueMapper;

    @Transactional
    public BanqueResponse createBanque(BanqueCreateRequest request) {
        log.info("Création d'une nouvelle banque: {}", request.getBanque());

        // Vérifications
        if (banqueRepository.existsByNumeroCompte(request.getNumeroCompte())) {
            throw new IllegalArgumentException("Un compte avec ce numéro existe déjà");
        }

        // Créer et sauvegarder la banque
        Banque banque = banqueMapper.toEntity(request);
        Banque savedBanque = banqueRepository.save(banque);
        BanqueResponse response = banqueMapper.toResponse(savedBanque);

        // Publier l'événement
        banqueEventProducer.publishBanqueCreated(response);

        log.info("Banque créée avec succès: {}", savedBanque.getIdBanque());
        return response;
    }

    @Transactional
    public BanqueResponse updateBanque(UUID banqueId, BanqueUpdateRequest request) {
        log.info("Mise à jour de la banque: {}", banqueId);

        Banque banque = banqueRepository.findById(banqueId)
                .orElseThrow(() -> new IllegalArgumentException("Banque non trouvée: " + banqueId));

        // Vérifier si le numéro de compte est déjà utilisé par une autre banque
        if (request.getNumeroCompte() != null &&
            !request.getNumeroCompte().equals(banque.getNumeroCompte()) &&
            banqueRepository.existsByNumeroCompte(request.getNumeroCompte())) {
            throw new IllegalArgumentException("Un compte avec ce numéro existe déjà");
        }

        // Mise à jour
        banqueMapper.updateEntityFromRequest(request, banque);
        Banque updatedBanque = banqueRepository.save(banque);
        BanqueResponse response = banqueMapper.toResponse(updatedBanque);

        // Publier l'événement
        banqueEventProducer.publishBanqueUpdated(response);

        log.info("Banque mise à jour avec succès: {}", banqueId);
        return response;
    }

    @Transactional(readOnly = true)
    public BanqueResponse getBanqueById(UUID banqueId) {
        log.info("Récupération de la banque: {}", banqueId);

        Banque banque = banqueRepository.findById(banqueId)
                .orElseThrow(() -> new IllegalArgumentException("Banque non trouvée: " + banqueId));

        return banqueMapper.toResponse(banque);
    }

    @Transactional(readOnly = true)
    public BanqueResponse getBanqueByNumeroCompte(String numeroCompte) {
        log.info("Récupération de la banque par numéro de compte: {}", numeroCompte);

        Banque banque = banqueRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new IllegalArgumentException("Banque non trouvée avec numéro de compte: " + numeroCompte));

        return banqueMapper.toResponse(banque);
    }

    @Transactional(readOnly = true)
    public List<BanqueResponse> getAllBanques() {
        log.info("Récupération de toutes les banques");
        List<Banque> banques = banqueRepository.findAll();
        return banqueMapper.toResponseList(banques);
    }

    @Transactional(readOnly = true)
    public Page<BanqueResponse> getAllBanques(Pageable pageable) {
        log.info("Récupération de toutes les banques avec pagination");
        return banqueRepository.findAll(pageable).map(banqueMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<BanqueResponse> getBanquesByName(String nomBanque) {
        log.info("Récupération des banques par nom: {}", nomBanque);
        List<Banque> banques = banqueRepository.findByBanque(nomBanque);
        return banqueMapper.toResponseList(banques);
    }

    @Transactional(readOnly = true)
    public List<BanqueResponse> searchBanquesByName(String nomBanque) {
        log.info("Recherche des banques par nom: {}", nomBanque);
        List<Banque> banques = banqueRepository.findByBanqueContaining(nomBanque);
        return banqueMapper.toResponseList(banques);
    }

    @Transactional(readOnly = true)
    public List<BanqueResponse> searchBanquesByNumeroCompte(String numeroCompte) {
        log.info("Recherche des banques par numéro de compte: {}", numeroCompte);
        List<Banque> banques = banqueRepository.findByNumeroCompteContaining(numeroCompte);
        return banqueMapper.toResponseList(banques);
    }

    @Transactional
    public void deleteBanque(UUID banqueId) {
        log.info("Suppression de la banque: {}", banqueId);

        if (!banqueRepository.existsById(banqueId)) {
            throw new IllegalArgumentException("Banque non trouvée: " + banqueId);
        }

        banqueRepository.deleteById(banqueId);

        // Publier l'événement
        banqueEventProducer.publishBanqueDeleted(banqueId);

        log.info("Banque supprimée avec succès: {}", banqueId);
    }
}
