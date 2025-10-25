package com.example.account.service;

import com.example.account.dto.request.BonAchatCreateRequest;
import com.example.account.dto.request.BonAchatUpdateRequest;
import com.example.account.dto.response.BonAchatResponse;
import com.example.account.mapper.BonAchatMapper;
import com.example.account.model.entity.BonAchat;
import com.example.account.repository.BonAchatRepository;
import com.example.account.service.producer.BonAchatEventProducer;
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
public class BonAchatService {

    private final BonAchatRepository bonAchatRepository;
    private final BonAchatEventProducer bonAchatEventProducer;
    private final BonAchatMapper bonAchatMapper;

    @Transactional
    public BonAchatResponse createBonAchat(BonAchatCreateRequest request) {
        log.info("Création d'un nouveau bon d'achat: {}", request.getNumeroBon());

        if (bonAchatRepository.existsByNumeroBon(request.getNumeroBon())) {
            throw new IllegalArgumentException("Un bon d'achat avec ce numéro existe déjà");
        }

        BonAchat bonAchat = bonAchatMapper.toEntity(request);
        BonAchat savedBonAchat = bonAchatRepository.save(bonAchat);
        BonAchatResponse response = bonAchatMapper.toResponse(savedBonAchat);

        bonAchatEventProducer.publishBonAchatCreated(response);

        log.info("Bon d'achat créé avec succès: {}", savedBonAchat.getIdBonAchat());
        return response;
    }

    @Transactional
    public BonAchatResponse updateBonAchat(UUID bonAchatId, BonAchatUpdateRequest request) {
        log.info("Mise à jour du bon d'achat: {}", bonAchatId);

        BonAchat bonAchat = bonAchatRepository.findById(bonAchatId)
                .orElseThrow(() -> new IllegalArgumentException("Bon d'achat non trouvé: " + bonAchatId));

        bonAchatMapper.updateEntityFromRequest(request, bonAchat);
        BonAchat updatedBonAchat = bonAchatRepository.save(bonAchat);
        BonAchatResponse response = bonAchatMapper.toResponse(updatedBonAchat);

        bonAchatEventProducer.publishBonAchatUpdated(response);

        log.info("Bon d'achat mis à jour avec succès: {}", bonAchatId);
        return response;
    }

    @Transactional(readOnly = true)
    public BonAchatResponse getBonAchatById(UUID bonAchatId) {
        log.info("Récupération du bon d'achat: {}", bonAchatId);

        BonAchat bonAchat = bonAchatRepository.findById(bonAchatId)
                .orElseThrow(() -> new IllegalArgumentException("Bon d'achat non trouvé: " + bonAchatId));

        return bonAchatMapper.toResponse(bonAchat);
    }

    @Transactional(readOnly = true)
    public BonAchatResponse getBonAchatByNumero(String numeroBon) {
        log.info("Récupération du bon d'achat par numéro: {}", numeroBon);

        BonAchat bonAchat = bonAchatRepository.findByNumeroBon(numeroBon)
                .orElseThrow(() -> new IllegalArgumentException("Bon d'achat non trouvé avec numéro: " + numeroBon));

        return bonAchatMapper.toResponse(bonAchat);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getAllBonAchats() {
        log.info("Récupération de tous les bons d'achat");
        List<BonAchat> bonAchats = bonAchatRepository.findAll();
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional(readOnly = true)
    public Page<BonAchatResponse> getAllBonAchats(Pageable pageable) {
        log.info("Récupération de tous les bons d'achat avec pagination");
        return bonAchatRepository.findAll(pageable).map(bonAchatMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getBonAchatsByFournisseur(UUID idFournisseur) {
        log.info("Récupération des bons d'achat du fournisseur: {}", idFournisseur);
        List<BonAchat> bonAchats = bonAchatRepository.findByIdFournisseur(idFournisseur);
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getBonAchatsByBonCommande(UUID idBonCommande) {
        log.info("Récupération des bons d'achat pour le bon de commande: {}", idBonCommande);
        List<BonAchat> bonAchats = bonAchatRepository.findByIdBonCommande(idBonCommande);
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getBonAchatsByStatut(String statut) {
        log.info("Récupération des bons d'achat par statut: {}", statut);
        List<BonAchat> bonAchats = bonAchatRepository.findByStatut(statut);
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getBonAchatsByDevise(String devise) {
        log.info("Récupération des bons d'achat par devise: {}", devise);
        List<BonAchat> bonAchats = bonAchatRepository.findByDevise(devise);
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getBonAchatsByDateAchatBetween(LocalDate startDate, LocalDate endDate) {
        log.info("Récupération des bons d'achat entre {} et {}", startDate, endDate);
        List<BonAchat> bonAchats = bonAchatRepository.findByDateAchatBetween(startDate, endDate);
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getBonAchatsByDateLivraisonBetween(LocalDate startDate, LocalDate endDate) {
        log.info("Récupération des bons d'achat avec livraison entre {} et {}", startDate, endDate);
        List<BonAchat> bonAchats = bonAchatRepository.findByDateLivraisonBetween(startDate, endDate);
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getBonAchatsByMontantBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        log.info("Récupération des bons d'achat entre {} et {}", minAmount, maxAmount);
        List<BonAchat> bonAchats = bonAchatRepository.findByMontantTotalBetween(minAmount, maxAmount);
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getBonAchatsByFournisseurAndStatut(UUID idFournisseur, String statut) {
        log.info("Récupération des bons d'achat du fournisseur {} avec statut {}", idFournisseur, statut);
        List<BonAchat> bonAchats = bonAchatRepository.findByFournisseurAndStatut(idFournisseur, statut);
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> searchBonAchatsByNumero(String numeroBon) {
        log.info("Recherche des bons d'achat par numéro: {}", numeroBon);
        List<BonAchat> bonAchats = bonAchatRepository.findByNumeroBonContaining(numeroBon);
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> searchBonAchatsByFournisseur(String nomFournisseur) {
        log.info("Recherche des bons d'achat par fournisseur: {}", nomFournisseur);
        List<BonAchat> bonAchats = bonAchatRepository.findByNomFournisseurContaining(nomFournisseur);
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> searchBonAchatsByFactureFournisseur(String numeroFacture) {
        log.info("Recherche des bons d'achat par facture fournisseur: {}", numeroFacture);
        List<BonAchat> bonAchats = bonAchatRepository.findByNumeroFactureFournisseurContaining(numeroFacture);
        return bonAchatMapper.toResponseList(bonAchats);
    }

    @Transactional
    public void deleteBonAchat(UUID bonAchatId) {
        log.info("Suppression du bon d'achat: {}", bonAchatId);

        if (!bonAchatRepository.existsById(bonAchatId)) {
            throw new IllegalArgumentException("Bon d'achat non trouvé: " + bonAchatId);
        }

        bonAchatRepository.deleteById(bonAchatId);
        bonAchatEventProducer.publishBonAchatDeleted(bonAchatId);

        log.info("Bon d'achat supprimé avec succès: {}", bonAchatId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalMontantByFournisseur(UUID idFournisseur) {
        log.info("Calcul du montant total des achats pour le fournisseur: {}", idFournisseur);
        BigDecimal total = bonAchatRepository.sumMontantByFournisseur(idFournisseur);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalMontantByStatut(String statut) {
        log.info("Calcul du montant total des achats par statut: {}", statut);
        BigDecimal total = bonAchatRepository.sumMontantByStatut(statut);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Long countByStatut(String statut) {
        return bonAchatRepository.countByStatut(statut);
    }

    @Transactional(readOnly = true)
    public Long countByFournisseur(UUID idFournisseur) {
        return bonAchatRepository.countByFournisseur(idFournisseur);
    }

    @Transactional
    public BonAchatResponse updateStatut(UUID bonAchatId, String nouveauStatut) {
        log.info("Mise à jour du statut du bon d'achat {} vers {}", bonAchatId, nouveauStatut);

        BonAchat bonAchat = bonAchatRepository.findById(bonAchatId)
                .orElseThrow(() -> new IllegalArgumentException("Bon d'achat non trouvé: " + bonAchatId));

        bonAchat.setStatut(nouveauStatut);
        bonAchat.setUpdatedAt(LocalDateTime.now());

        BonAchat updatedBonAchat = bonAchatRepository.save(bonAchat);
        BonAchatResponse response = bonAchatMapper.toResponse(updatedBonAchat);

        bonAchatEventProducer.publishBonAchatUpdated(response);

        log.info("Statut du bon d'achat mis à jour avec succès: {}", bonAchatId);
        return response;
    }

    @Transactional
    public BonAchatResponse validerBonAchat(UUID bonAchatId, String validatedBy) {
        log.info("Validation du bon d'achat: {}", bonAchatId);

        BonAchat bonAchat = bonAchatRepository.findById(bonAchatId)
                .orElseThrow(() -> new IllegalArgumentException("Bon d'achat non trouvé: " + bonAchatId));

        bonAchat.setStatut("VALIDE");
        bonAchat.setValidatedAt(LocalDateTime.now());
        bonAchat.setValidatedBy(validatedBy);
        bonAchat.setUpdatedAt(LocalDateTime.now());

        BonAchat validatedBonAchat = bonAchatRepository.save(bonAchat);
        BonAchatResponse response = bonAchatMapper.toResponse(validatedBonAchat);

        bonAchatEventProducer.publishBonAchatUpdated(response);

        log.info("Bon d'achat validé avec succès: {}", bonAchatId);
        return response;
    }
}
