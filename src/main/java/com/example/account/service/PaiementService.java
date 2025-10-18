package com.example.account.service;

import com.example.account.dto.request.PaiementCreateRequest;
import com.example.account.dto.request.PaiementUpdateRequest;
import com.example.account.dto.response.PaiementResponse;
import com.example.account.mapper.PaiementMapper;
import com.example.account.model.entity.Paiement;
import com.example.account.model.enums.TypePaiement;
import com.example.account.repository.PaiementRepository;
import com.example.account.service.producer.PaiementEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final PaiementMapper paiementMapper;
    private final PaiementEventProducer paiementEventProducer;
    private final FactureService factureService;

    @Transactional
    public PaiementResponse createPaiement(PaiementCreateRequest request) {
        log.info("Création d'un nouveau paiement pour le client: {}", request.getIdClient());

        // Créer et sauvegarder le paiement
        Paiement paiement = paiementMapper.toEntity(request);
        Paiement savedPaiement = paiementRepository.save(paiement);

        // Si le paiement est lié à une facture, mettre à jour la facture
        if (request.getIdFacture() != null) {
            try {
                factureService.enregistrerPaiement(request.getIdFacture(), request.getMontant());
            } catch (Exception e) {
                log.error("Erreur lors de la mise à jour de la facture: {}", e.getMessage());
            }
        }

        PaiementResponse response = paiementMapper.toResponse(savedPaiement);

        // Publier l'événement
        paiementEventProducer.publishPaiementCreated(response);

        log.info("Paiement créé avec succès: {}", savedPaiement.getIdPaiement());
        return response;
    }

    @Transactional
    public PaiementResponse updatePaiement(UUID paiementId, PaiementUpdateRequest request) {
        log.info("Mise à jour du paiement: {}", paiementId);

        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement non trouvé: " + paiementId));

        paiementMapper.updateEntityFromRequest(request, paiement);
        Paiement updatedPaiement = paiementRepository.save(paiement);
        PaiementResponse response = paiementMapper.toResponse(updatedPaiement);

        // Publier l'événement
        paiementEventProducer.publishPaiementUpdated(response);

        log.info("Paiement mis à jour avec succès: {}", paiementId);
        return response;
    }

    @Transactional(readOnly = true)
    public PaiementResponse getPaiementById(UUID paiementId) {
        log.info("Récupération du paiement: {}", paiementId);

        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement non trouvé: " + paiementId));

        return paiementMapper.toResponse(paiement);
    }

    @Transactional(readOnly = true)
    public List<PaiementResponse> getAllPaiements() {
        log.info("Récupération de tous les paiements");
        List<Paiement> paiements = paiementRepository.findAll();
        return paiementMapper.toResponseList(paiements);
    }

    @Transactional(readOnly = true)
    public Page<PaiementResponse> getAllPaiements(Pageable pageable) {
        log.info("Récupération de tous les paiements avec pagination");
        Page<Paiement> paiements = paiementRepository.findAll(pageable);
        return paiements.map(paiementMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PaiementResponse> getPaiementsByClient(UUID clientId) {
        log.info("Récupération des paiements du client: {}", clientId);
        List<Paiement> paiements = paiementRepository.findByIdClient(clientId);
        return paiementMapper.toResponseList(paiements);
    }

    @Transactional(readOnly = true)
    public List<PaiementResponse> getPaiementsByFacture(UUID factureId) {
        log.info("Récupération des paiements de la facture: {}", factureId);
        List<Paiement> paiements = paiementRepository.findByIdFacture(factureId);
        return paiementMapper.toResponseList(paiements);
    }

    @Transactional(readOnly = true)
    public List<PaiementResponse> getPaiementsByModePaiement(TypePaiement modePaiement) {
        log.info("Récupération des paiements par mode de paiement: {}", modePaiement);
        List<Paiement> paiements = paiementRepository.findByModePaiement(modePaiement);
        return paiementMapper.toResponseList(paiements);
    }

    @Transactional(readOnly = true)
    public List<PaiementResponse> getPaiementsByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Récupération des paiements entre {} et {}", dateDebut, dateFin);
        List<Paiement> paiements = paiementRepository.findByDateBetween(dateDebut, dateFin);
        return paiementMapper.toResponseList(paiements);
    }

    @Transactional
    public void deletePaiement(UUID paiementId) {
        log.info("Suppression du paiement: {}", paiementId);

        if (!paiementRepository.existsById(paiementId)) {
            throw new IllegalArgumentException("Paiement non trouvé: " + paiementId);
        }

        paiementRepository.deleteById(paiementId);

        // Publier l'événement
        paiementEventProducer.publishPaiementDeleted(paiementId);

        log.info("Paiement supprimé avec succès: {}", paiementId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaiementsByClient(UUID clientId) {
        log.info("Calcul du total des paiements du client: {}", clientId);
        BigDecimal total = paiementRepository.sumMontantByClient(clientId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaiementsByFacture(UUID factureId) {
        log.info("Calcul du total des paiements de la facture: {}", factureId);
        BigDecimal total = paiementRepository.sumMontantByFacture(factureId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaiementsByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Calcul du total des paiements entre {} et {}", dateDebut, dateFin);
        BigDecimal total = paiementRepository.sumMontantByDateBetween(dateDebut, dateFin);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Long countPaiementsByClient(UUID clientId) {
        return paiementRepository.countByIdClient(clientId);
    }

    @Transactional(readOnly = true)
    public Long countPaiementsByModePaiement(TypePaiement modePaiement) {
        return paiementRepository.countByModePaiement(modePaiement);
    }
}
