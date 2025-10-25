package com.example.account.service;

import com.example.account.dto.request.BonCommandeCreateRequest;
import com.example.account.dto.request.BonCommandeUpdateRequest;
import com.example.account.dto.response.BonCommandeResponse;
import com.example.account.mapper.BonCommandeMapper;
import com.example.account.model.entity.BonCommande;
import com.example.account.repository.BonCommandeRepository;
import com.example.account.service.producer.BonCommandeEventProducer;
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

        // Vérifications
        if (bonCommandeRepository.existsByNumeroCommande(request.getNumeroCommande())) {
            throw new IllegalArgumentException("Un bon de commande avec ce numéro existe déjà");
        }

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
    public BonCommandeResponse updateBonCommande(UUID bonCommandeId, BonCommandeUpdateRequest request) {
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

    @Transactional(readOnly = true)
    public List<BonCommandeResponse> getBonCommandesByFournisseur(UUID idFournisseur) {
        log.info("Récupération des bons de commande du fournisseur: {}", idFournisseur);
        List<BonCommande> bonCommandes = bonCommandeRepository.findByIdFournisseur(idFournisseur);
        return bonCommandeMapper.toResponseList(bonCommandes);
    }

    @Transactional(readOnly = true)
    public List<BonCommandeResponse> getBonCommandesByStatut(String statut) {
        log.info("Récupération des bons de commande par statut: {}", statut);
        List<BonCommande> bonCommandes = bonCommandeRepository.findByStatut(statut);
        return bonCommandeMapper.toResponseList(bonCommandes);
    }

    @Transactional(readOnly = true)
    public List<BonCommandeResponse> getBonCommandesByDevise(String devise) {
        log.info("Récupération des bons de commande par devise: {}", devise);
        List<BonCommande> bonCommandes = bonCommandeRepository.findByDevise(devise);
        return bonCommandeMapper.toResponseList(bonCommandes);
    }

    @Transactional(readOnly = true)
    public List<BonCommandeResponse> getBonCommandesByDateCommandeBetween(LocalDate startDate, LocalDate endDate) {
        log.info("Récupération des bons de commande entre {} et {}", startDate, endDate);
        List<BonCommande> bonCommandes = bonCommandeRepository.findByDateCommandeBetween(startDate, endDate);
        return bonCommandeMapper.toResponseList(bonCommandes);
    }

    @Transactional(readOnly = true)
    public List<BonCommandeResponse> getBonCommandesByDateLivraisonBetween(LocalDate startDate, LocalDate endDate) {
        log.info("Récupération des bons de commande avec livraison entre {} et {}", startDate, endDate);
        List<BonCommande> bonCommandes = bonCommandeRepository.findByDateLivraisonPrevueBetween(startDate, endDate);
        return bonCommandeMapper.toResponseList(bonCommandes);
    }

    @Transactional(readOnly = true)
    public List<BonCommandeResponse> getBonCommandesByMontantBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        log.info("Récupération des bons de commande entre {} et {}", minAmount, maxAmount);
        List<BonCommande> bonCommandes = bonCommandeRepository.findByMontantTotalBetween(minAmount, maxAmount);
        return bonCommandeMapper.toResponseList(bonCommandes);
    }

    @Transactional(readOnly = true)
    public List<BonCommandeResponse> getBonCommandesByFournisseurAndStatut(UUID idFournisseur, String statut) {
        log.info("Récupération des bons de commande du fournisseur {} avec statut {}", idFournisseur, statut);
        List<BonCommande> bonCommandes = bonCommandeRepository.findByFournisseurAndStatut(idFournisseur, statut);
        return bonCommandeMapper.toResponseList(bonCommandes);
    }

    @Transactional(readOnly = true)
    public List<BonCommandeResponse> searchBonCommandesByNumero(String numeroCommande) {
        log.info("Recherche des bons de commande par numéro: {}", numeroCommande);
        List<BonCommande> bonCommandes = bonCommandeRepository.findByNumeroCommandeContaining(numeroCommande);
        return bonCommandeMapper.toResponseList(bonCommandes);
    }

    @Transactional(readOnly = true)
    public List<BonCommandeResponse> searchBonCommandesByFournisseur(String nomFournisseur) {
        log.info("Recherche des bons de commande par fournisseur: {}", nomFournisseur);
        List<BonCommande> bonCommandes = bonCommandeRepository.findByNomFournisseurContaining(nomFournisseur);
        return bonCommandeMapper.toResponseList(bonCommandes);
    }

    @Transactional
    public void deleteBonCommande(UUID bonCommandeId) {
        log.info("Suppression du bon de commande: {}", bonCommandeId);

        if (!bonCommandeRepository.existsById(bonCommandeId)) {
            throw new IllegalArgumentException("Bon de commande non trouvé: " + bonCommandeId);
        }

        bonCommandeRepository.deleteById(bonCommandeId);

        // Publier l'événement
        bonCommandeEventProducer.publishBonCommandeDeleted(bonCommandeId);

        log.info("Bon de commande supprimé avec succès: {}", bonCommandeId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalMontantByFournisseur(UUID idFournisseur) {
        log.info("Calcul du montant total des commandes pour le fournisseur: {}", idFournisseur);
        BigDecimal total = bonCommandeRepository.sumMontantByFournisseur(idFournisseur);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalMontantByStatut(String statut) {
        log.info("Calcul du montant total des commandes par statut: {}", statut);
        BigDecimal total = bonCommandeRepository.sumMontantByStatut(statut);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Long countByStatut(String statut) {
        return bonCommandeRepository.countByStatut(statut);
    }

    @Transactional(readOnly = true)
    public Long countByFournisseur(UUID idFournisseur) {
        return bonCommandeRepository.countByFournisseur(idFournisseur);
    }

    @Transactional
    public BonCommandeResponse updateStatut(UUID bonCommandeId, String nouveauStatut) {
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

    @Transactional
    public BonCommandeResponse validerBonCommande(UUID bonCommandeId, String validatedBy) {
        log.info("Validation du bon de commande: {}", bonCommandeId);

        BonCommande bonCommande = bonCommandeRepository.findById(bonCommandeId)
                .orElseThrow(() -> new IllegalArgumentException("Bon de commande non trouvé: " + bonCommandeId));

        bonCommande.setStatut("VALIDE");
        bonCommande.setValidatedAt(LocalDateTime.now());
        bonCommande.setValidatedBy(validatedBy);
        bonCommande.setUpdatedAt(LocalDateTime.now());

        BonCommande validatedBonCommande = bonCommandeRepository.save(bonCommande);
        BonCommandeResponse response = bonCommandeMapper.toResponse(validatedBonCommande);

        // Publier l'événement
        bonCommandeEventProducer.publishBonCommandeUpdated(response);

        log.info("Bon de commande validé avec succès: {}", bonCommandeId);
        return response;
    }
}
