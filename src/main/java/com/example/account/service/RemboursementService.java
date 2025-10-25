package com.example.account.service;

import com.example.account.dto.request.RemboursementCreateRequest;
import com.example.account.dto.request.RemboursementUpdateRequest;
import com.example.account.dto.response.RemboursementResponse;
import com.example.account.mapper.RemboursementMapper;
import com.example.account.model.entity.Remboursement;
import com.example.account.repository.RemboursementRepository;
import com.example.account.service.producer.RemboursementEventProducer;
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
public class RemboursementService {

    private final RemboursementRepository remboursementRepository;
    private final RemboursementEventProducer remboursementEventProducer;
    private final RemboursementMapper remboursementMapper;

    @Transactional
    public RemboursementResponse createRemboursement(RemboursementCreateRequest request) {
        log.info("Création d'un nouveau remboursement pour le client: {}", request.getIdClient());

        // Créer et sauvegarder le remboursement
        Remboursement remboursement = remboursementMapper.toEntity(request);
        Remboursement savedRemboursement = remboursementRepository.save(remboursement);
        RemboursementResponse response = remboursementMapper.toResponse(savedRemboursement);

        // Publier l'événement
        remboursementEventProducer.publishRemboursementCreated(response);

        log.info("Remboursement créé avec succès: {}", savedRemboursement.getIdRemboursement());
        return response;
    }

    @Transactional
    public RemboursementResponse updateRemboursement(UUID remboursementId, RemboursementUpdateRequest request) {
        log.info("Mise à jour du remboursement: {}", remboursementId);

        Remboursement remboursement = remboursementRepository.findById(remboursementId)
                .orElseThrow(() -> new IllegalArgumentException("Remboursement non trouvé: " + remboursementId));

        // Mise à jour
        remboursementMapper.updateEntityFromRequest(request, remboursement);
        Remboursement updatedRemboursement = remboursementRepository.save(remboursement);
        RemboursementResponse response = remboursementMapper.toResponse(updatedRemboursement);

        // Publier l'événement
        remboursementEventProducer.publishRemboursementUpdated(response);

        log.info("Remboursement mis à jour avec succès: {}", remboursementId);
        return response;
    }

    @Transactional(readOnly = true)
    public RemboursementResponse getRemboursementById(UUID remboursementId) {
        log.info("Récupération du remboursement: {}", remboursementId);

        Remboursement remboursement = remboursementRepository.findById(remboursementId)
                .orElseThrow(() -> new IllegalArgumentException("Remboursement non trouvé: " + remboursementId));

        return remboursementMapper.toResponse(remboursement);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getAllRemboursements() {
        log.info("Récupération de tous les remboursements");
        List<Remboursement> remboursements = remboursementRepository.findAll();
        return remboursementMapper.toResponseList(remboursements);
    }

    @Transactional(readOnly = true)
    public Page<RemboursementResponse> getAllRemboursements(Pageable pageable) {
        log.info("Récupération de tous les remboursements avec pagination");
        return remboursementRepository.findAll(pageable).map(remboursementMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getRemboursementsByClient(UUID idClient) {
        log.info("Récupération des remboursements du client: {}", idClient);
        List<Remboursement> remboursements = remboursementRepository.findByIdClient(idClient);
        return remboursementMapper.toResponseList(remboursements);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getRemboursementsByFacture(UUID idFacture) {
        log.info("Récupération des remboursements de la facture: {}", idFacture);
        List<Remboursement> remboursements = remboursementRepository.findByIdFacture(idFacture);
        return remboursementMapper.toResponseList(remboursements);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getRemboursementsByStatut(String statut) {
        log.info("Récupération des remboursements par statut: {}", statut);
        List<Remboursement> remboursements = remboursementRepository.findByStatut(statut);
        return remboursementMapper.toResponseList(remboursements);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getRemboursementsByDevise(String devise) {
        log.info("Récupération des remboursements par devise: {}", devise);
        List<Remboursement> remboursements = remboursementRepository.findByDevise(devise);
        return remboursementMapper.toResponseList(remboursements);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getRemboursementsByDateFacturationBetween(LocalDate startDate, LocalDate endDate) {
        log.info("Récupération des remboursements entre {} et {}", startDate, endDate);
        List<Remboursement> remboursements = remboursementRepository.findByDateFacturationBetween(startDate, endDate);
        return remboursementMapper.toResponseList(remboursements);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getRemboursementsByDateEcheanceBetween(LocalDate startDate, LocalDate endDate) {
        log.info("Récupération des remboursements avec échéance entre {} et {}", startDate, endDate);
        List<Remboursement> remboursements = remboursementRepository.findByDateEcheanceBetween(startDate, endDate);
        return remboursementMapper.toResponseList(remboursements);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getRemboursementsByDateComptableBetween(LocalDate startDate, LocalDate endDate) {
        log.info("Récupération des remboursements avec date comptable entre {} et {}", startDate, endDate);
        List<Remboursement> remboursements = remboursementRepository.findByDateComptableBetween(startDate, endDate);
        return remboursementMapper.toResponseList(remboursements);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getRemboursementsByMontantBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        log.info("Récupération des remboursements entre {} et {}", minAmount, maxAmount);
        List<Remboursement> remboursements = remboursementRepository.findByMontantBetween(minAmount, maxAmount);
        return remboursementMapper.toResponseList(remboursements);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getOverdueRemboursements() {
        log.info("Récupération des remboursements en retard");
        List<Remboursement> remboursements = remboursementRepository.findOverdueRemboursements("EN_ATTENTE", LocalDate.now());
        return remboursementMapper.toResponseList(remboursements);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getRemboursementsByClientAndStatut(UUID idClient, String statut) {
        log.info("Récupération des remboursements du client {} avec statut {}", idClient, statut);
        List<Remboursement> remboursements = remboursementRepository.findByClientAndStatut(idClient, statut);
        return remboursementMapper.toResponseList(remboursements);
    }

    @Transactional
    public void deleteRemboursement(UUID remboursementId) {
        log.info("Suppression du remboursement: {}", remboursementId);

        if (!remboursementRepository.existsById(remboursementId)) {
            throw new IllegalArgumentException("Remboursement non trouvé: " + remboursementId);
        }

        remboursementRepository.deleteById(remboursementId);

        // Publier l'événement
        remboursementEventProducer.publishRemboursementDeleted(remboursementId);

        log.info("Remboursement supprimé avec succès: {}", remboursementId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalMontantByClient(UUID idClient) {
        log.info("Calcul du montant total des remboursements pour le client: {}", idClient);
        BigDecimal total = remboursementRepository.sumMontantByClient(idClient);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalMontantByStatut(String statut) {
        log.info("Calcul du montant total des remboursements par statut: {}", statut);
        BigDecimal total = remboursementRepository.sumMontantByStatut(statut);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Long countByStatut(String statut) {
        return remboursementRepository.countByStatut(statut);
    }

    @Transactional(readOnly = true)
    public Long countByClient(UUID idClient) {
        return remboursementRepository.countByIdClient(idClient);
    }

    @Transactional
    public RemboursementResponse updateStatut(UUID remboursementId, String nouveauStatut) {
        log.info("Mise à jour du statut du remboursement {} vers {}", remboursementId, nouveauStatut);

        Remboursement remboursement = remboursementRepository.findById(remboursementId)
                .orElseThrow(() -> new IllegalArgumentException("Remboursement non trouvé: " + remboursementId));

        remboursement.setStatut(nouveauStatut);
        Remboursement updatedRemboursement = remboursementRepository.save(remboursement);
        RemboursementResponse response = remboursementMapper.toResponse(updatedRemboursement);

        // Publier l'événement
        remboursementEventProducer.publishRemboursementUpdated(response);

        log.info("Statut du remboursement mis à jour avec succès: {}", remboursementId);
        return response;
    }
}
