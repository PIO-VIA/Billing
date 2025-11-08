package com.example.account.service;

import com.example.account.model.entity.BonLivraison;
import com.example.account.repository.BonLivraisonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonLivraisonService {

    private final BonLivraisonRepository bonLivraisonRepository;

    @Transactional(readOnly = true)
    public BonLivraison getBonLivraisonById(UUID bonLivraisonId) {
        log.info("Récupération du bon de livraison: {}", bonLivraisonId);
        return bonLivraisonRepository.findById(bonLivraisonId)
                .orElseThrow(() -> new IllegalArgumentException("Bon de livraison non trouvé: " + bonLivraisonId));
    }

    @Transactional(readOnly = true)
    public BonLivraison getBonLivraisonByNumero(String numeroBonLivraison) {
        log.info("Récupération du bon de livraison par numéro: {}", numeroBonLivraison);
        return bonLivraisonRepository.findByNumeroBonLivraison(numeroBonLivraison)
                .orElseThrow(() -> new IllegalArgumentException("Bon de livraison non trouvé: " + numeroBonLivraison));
    }

    @Transactional(readOnly = true)
    public List<BonLivraison> getBonLivraisonsByClient(UUID idClient) {
        log.info("Récupération des bons de livraison pour le client: {}", idClient);
        return bonLivraisonRepository.findByIdClient(idClient);
    }

    @Transactional(readOnly = true)
    public List<BonLivraison> getBonLivraisonsByFacture(UUID idFacture) {
        log.info("Récupération des bons de livraison pour la facture: {}", idFacture);
        return bonLivraisonRepository.findByIdFacture(idFacture);
    }

    @Transactional(readOnly = true)
    public List<BonLivraison> getBonLivraisonsByStatut(String statut) {
        log.info("Récupération des bons de livraison par statut: {}", statut);
        return bonLivraisonRepository.findByStatut(statut);
    }

    @Transactional(readOnly = true)
    public List<BonLivraison> getBonLivraisonsByPeriode(LocalDate startDate, LocalDate endDate) {
        log.info("Récupération des bons de livraison entre {} et {}", startDate, endDate);
        return bonLivraisonRepository.findByDateLivraisonBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<BonLivraison> getLivraisonsEnRetard() {
        log.info("Récupération des livraisons en retard");
        return bonLivraisonRepository.findLivraisonsEnRetard(LocalDate.now());
    }

    @Transactional
    public BonLivraison marquerCommeEffectuee(UUID bonLivraisonId, String signatureClient) {
        log.info("Marquage du bon de livraison {} comme effectuée", bonLivraisonId);
        BonLivraison bonLivraison = getBonLivraisonById(bonLivraisonId);

        if (bonLivraison.getLivraisonEffectuee()) {
            throw new IllegalStateException("La livraison a déjà été effectuée");
        }

        bonLivraison.setLivraisonEffectuee(true);
        bonLivraison.setDateLivraisonEffective(LocalDateTime.now());
        bonLivraison.setSignatureClient(signatureClient);
        bonLivraison.setDateSignature(LocalDateTime.now());
        bonLivraison.setStatut("LIVRE");
        bonLivraison.setUpdatedAt(LocalDateTime.now());

        return bonLivraisonRepository.save(bonLivraison);
    }

    @Transactional
    public BonLivraison updateStatut(UUID bonLivraisonId, String nouveauStatut) {
        log.info("Mise à jour du statut du bon de livraison {} vers {}", bonLivraisonId, nouveauStatut);
        BonLivraison bonLivraison = getBonLivraisonById(bonLivraisonId);

        bonLivraison.setStatut(nouveauStatut);
        bonLivraison.setUpdatedAt(LocalDateTime.now());

        return bonLivraisonRepository.save(bonLivraison);
    }
}
