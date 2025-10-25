package com.example.account.service;

import com.example.account.model.entity.MouvementStock;
import com.example.account.repository.MouvementStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;

    @Transactional(readOnly = true)
    public MouvementStock getMouvementById(UUID mouvementId) {
        log.info("Récupération du mouvement: {}", mouvementId);
        return mouvementStockRepository.findById(mouvementId)
                .orElseThrow(() -> new IllegalArgumentException("Mouvement non trouvé: " + mouvementId));
    }

    @Transactional(readOnly = true)
    public MouvementStock getMouvementByNumero(String numeroMouvement) {
        log.info("Récupération du mouvement par numéro: {}", numeroMouvement);
        return mouvementStockRepository.findByNumeroMouvement(numeroMouvement)
                .orElseThrow(() -> new IllegalArgumentException("Mouvement non trouvé: " + numeroMouvement));
    }

    @Transactional(readOnly = true)
    public List<MouvementStock> getMouvementsByProduit(UUID idProduit) {
        log.info("Récupération des mouvements pour le produit: {}", idProduit);
        return mouvementStockRepository.findByIdProduit(idProduit);
    }

    @Transactional(readOnly = true)
    public List<MouvementStock> getMouvementsByType(String typeMouvement) {
        log.info("Récupération des mouvements par type: {}", typeMouvement);
        return mouvementStockRepository.findByTypeMouvement(typeMouvement);
    }

    @Transactional(readOnly = true)
    public List<MouvementStock> getMouvementsByPeriode(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Récupération des mouvements entre {} et {}", startDate, endDate);
        return mouvementStockRepository.findByDateMouvementBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<MouvementStock> getHistoriqueProduit(UUID idProduit, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Récupération de l'historique du produit {} entre {} et {}", idProduit, startDate, endDate);
        return mouvementStockRepository.findHistoriqueProduit(idProduit, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<MouvementStock> getMouvementsNonValides() {
        log.info("Récupération des mouvements non validés");
        return mouvementStockRepository.findMouvementsNonValides();
    }

    @Transactional
    public MouvementStock validerMouvement(UUID mouvementId, String validatedBy) {
        log.info("Validation du mouvement: {} par {}", mouvementId, validatedBy);
        MouvementStock mouvement = getMouvementById(mouvementId);

        if (mouvement.getValidated()) {
            throw new IllegalStateException("Le mouvement est déjà validé");
        }

        mouvement.setValidated(true);
        mouvement.setValidatedBy(validatedBy);
        mouvement.setValidatedAt(LocalDateTime.now());

        return mouvementStockRepository.save(mouvement);
    }
}
