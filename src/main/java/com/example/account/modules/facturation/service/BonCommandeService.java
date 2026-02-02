package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.BonCommandeCreateRequest;
import com.example.account.modules.facturation.dto.request.BonCommandeUpdateRequest;
import com.example.account.modules.facturation.dto.response.BonCommandeResponse;
import com.example.account.modules.facturation.mapper.BonCommandeMapper;
import com.example.account.modules.facturation.model.entity.BonCommande;
import com.example.account.modules.facturation.model.enums.StatusBonCommande;
import com.example.account.modules.facturation.repository.BonCommandeRepository;
import com.example.account.modules.facturation.service.producer.BonCommandeEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonCommandeService {

    private final BonCommandeRepository bonCommandeRepository;
    private final BonCommandeEventProducer bonCommandeEventProducer;
    private final BonCommandeMapper bonCommandeMapper;

    @Transactional
    public BonCommandeResponse createBonCommande(BonCommandeCreateRequest request) {
        log.info("Création d'un nouveau bon de commande: {}", request.getNumeroCommande());

        

        // Créer et sauvegarder le bon de commande
        BonCommande bonCommande = bonCommandeMapper.toEntity(request);
        BonCommande savedBonCommande = bonCommandeRepository.save(bonCommande);
        BonCommandeResponse response = bonCommandeMapper.toResponse(savedBonCommande);

        // Publier l'événement
        bonCommandeEventProducer.publishBonCommandeCreated(response);

        log.info("Bon de commande créé avec succès: {}", savedBonCommande.getIdBonCommande());
        return response;
    }

    @Transactional
    public BonCommandeResponse updateBonCommande(UUID bonCommandeId, BonCommandeCreateRequest request) {
        log.info("Mise à jour du bon de commande: {}", bonCommandeId);

        BonCommande bonCommande = bonCommandeRepository.findById(bonCommandeId)
                .orElseThrow(() -> new IllegalArgumentException("Bon de commande non trouvé: " + bonCommandeId));

        // Mise à jour
        bonCommandeMapper.updateEntityFromRequest(request, bonCommande);
        BonCommande updatedBonCommande = bonCommandeRepository.save(bonCommande);
        BonCommandeResponse response = bonCommandeMapper.toResponse(updatedBonCommande);

        // Publier l'événement
        bonCommandeEventProducer.publishBonCommandeUpdated(response);

        log.info("Bon de commande mis à jour avec succès: {}", bonCommandeId);
        return response;
    }

    @Transactional(readOnly = true)
    public BonCommandeResponse getBonCommandeById(UUID bonCommandeId) {
        log.info("Récupération du bon de commande: {}", bonCommandeId);

        BonCommande bonCommande = bonCommandeRepository.findById(bonCommandeId)
                .orElseThrow(() -> new IllegalArgumentException("Bon de commande non trouvé: " + bonCommandeId));

        return bonCommandeMapper.toResponse(bonCommande);
    }

    @Transactional(readOnly = true)
    public BonCommandeResponse getBonCommandeByNumero(String numeroCommande) {
        log.info("Récupération du bon de commande par numéro: {}", numeroCommande);

        BonCommande bonCommande = bonCommandeRepository.findByNumeroCommande(numeroCommande)
                .orElseThrow(() -> new IllegalArgumentException("Bon de commande non trouvé avec numéro: " + numeroCommande));

        return bonCommandeMapper.toResponse(bonCommande);
    }

    @Transactional(readOnly = true)
    public List<BonCommandeResponse> getAllBonCommandes() {
        log.info("Récupération de tous les bons de commande");
        List<BonCommande> bonCommandes = bonCommandeRepository.findAll();
        return bonCommandeMapper.toResponseList(bonCommandes);
    }

    @Transactional(readOnly = true)
    public Page<BonCommandeResponse> getAllBonCommandes(Pageable pageable) {
        log.info("Récupération de tous les bons de commande avec pagination");
        return bonCommandeRepository.findAll(pageable).map(bonCommandeMapper::toResponse);
    }

   

    @Transactional
    public BonCommandeResponse updateStatut(UUID bonCommandeId, StatusBonCommande nouveauStatut) {
        log.info("Mise à jour du statut du bon de commande {} vers {}", bonCommandeId, nouveauStatut);

        BonCommande bonCommande = bonCommandeRepository.findById(bonCommandeId)
                .orElseThrow(() -> new IllegalArgumentException("Bon de commande non trouvé: " + bonCommandeId));

        bonCommande.setStatut(nouveauStatut);
        bonCommande.setUpdatedAt(LocalDateTime.now());

        BonCommande updatedBonCommande = bonCommandeRepository.save(bonCommande);
        BonCommandeResponse response = bonCommandeMapper.toResponse(updatedBonCommande);

        // Publier l'événement
        bonCommandeEventProducer.publishBonCommandeUpdated(response);

        log.info("Statut du bon de commande mis à jour avec succès: {}", bonCommandeId);
        return response;
    }

    
}
