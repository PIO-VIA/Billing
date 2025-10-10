package com.example.account.service;

import com.example.account.dto.request.DevisCreateRequest;
import com.example.account.dto.response.DevisResponse;
import com.example.account.mapper.DevisMapper;
import com.example.account.model.entity.Client;
import com.example.account.model.entity.Devis;
import com.example.account.model.enums.StatutDevis;
import com.example.account.repository.ClientRepository;
import com.example.account.repository.DevisRepository;
import com.example.account.service.producer.DevisEventProducer;
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
public class DevisService {

    private final DevisRepository devisRepository;
    private final ClientRepository clientRepository;
    private final DevisMapper devisMapper;
    private final DevisEventProducer devisEventProducer;

    @Transactional
    public DevisResponse createDevis(DevisCreateRequest request) {
        log.info("Création d'un nouveau devis pour le client: {}", request.getIdClient());

        // Vérifier que le client existe
        Client client = clientRepository.findById(request.getIdClient())
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé: " + request.getIdClient()));

        // Créer le devis
        Devis devis = devisMapper.toEntity(request);
        devis.setIdDevis(UUID.randomUUID());
        devis.setNumeroDevis(generateNumeroDevis());
        devis.setCreatedAt(LocalDateTime.now());
        devis.setUpdatedAt(LocalDateTime.now());

        // Copier les informations du client
        devis.setNomClient(client.getUsername());
        devis.setAdresseClient(client.getAdresse());
        devis.setEmailClient(client.getEmail());
        devis.setTelephoneClient(client.getTelephone());

        // Calculer les montants si des lignes de devis sont fournies
        if (request.getLignesDevis() != null && !request.getLignesDevis().isEmpty()) {
            calculateMontants(devis);
        } else {
            devis.setMontantHT(BigDecimal.ZERO);
            devis.setMontantTVA(BigDecimal.ZERO);
            devis.setMontantTTC(BigDecimal.ZERO);
            devis.setMontantTotal(BigDecimal.ZERO);
        }

        Devis savedDevis = devisRepository.save(devis);
        DevisResponse response = devisMapper.toResponse(savedDevis);

        // Publier l'événement
        devisEventProducer.publishDevisCreated(response);

        log.info("Devis créé avec succès: {}", savedDevis.getNumeroDevis());
        return response;
    }

    @Transactional
    public DevisResponse updateDevis(UUID devisId, DevisCreateRequest request) {
        log.info("Mise à jour du devis: {}", devisId);

        Devis devis = devisRepository.findById(devisId)
                .orElseThrow(() -> new IllegalArgumentException("Devis non trouvé: " + devisId));

        devisMapper.updateEntityFromRequest(request, devis);
        devis.setUpdatedAt(LocalDateTime.now());

        // Recalculer les montants si nécessaire
        if (devis.getLignesDevis() != null && !devis.getLignesDevis().isEmpty()) {
            calculateMontants(devis);
        }

        Devis updatedDevis = devisRepository.save(devis);
        DevisResponse response = devisMapper.toResponse(updatedDevis);

        // Publier l'événement
        devisEventProducer.publishDevisUpdated(response);

        log.info("Devis mis à jour avec succès: {}", devisId);
        return response;
    }

    @Transactional(readOnly = true)
    public DevisResponse getDevisById(UUID devisId) {
        log.info("Récupération du devis: {}", devisId);

        Devis devis = devisRepository.findById(devisId)
                .orElseThrow(() -> new IllegalArgumentException("Devis non trouvé: " + devisId));

        return devisMapper.toResponse(devis);
    }

    @Transactional(readOnly = true)
    public DevisResponse getDevisByNumero(String numeroDevis) {
        log.info("Récupération du devis par numéro: {}", numeroDevis);

        Devis devis = devisRepository.findByNumeroDevis(numeroDevis)
                .orElseThrow(() -> new IllegalArgumentException("Devis non trouvé avec numéro: " + numeroDevis));

        return devisMapper.toResponse(devis);
    }

    @Transactional(readOnly = true)
    public List<DevisResponse> getAllDevis() {
        log.info("Récupération de tous les devis");
        List<Devis> devisList = devisRepository.findAll();
        return devisMapper.toResponseList(devisList);
    }

    @Transactional(readOnly = true)
    public Page<DevisResponse> getAllDevis(Pageable pageable) {
        log.info("Récupération de tous les devis avec pagination");
        Page<Devis> devisList = devisRepository.findAll(pageable);
        return devisList.map(devisMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<DevisResponse> getDevisByClient(UUID clientId) {
        log.info("Récupération des devis du client: {}", clientId);
        List<Devis> devisList = devisRepository.findByIdClient(clientId);
        return devisMapper.toResponseList(devisList);
    }

    @Transactional(readOnly = true)
    public List<DevisResponse> getDevisByStatut(StatutDevis statut) {
        log.info("Récupération des devis par statut: {}", statut);
        List<Devis> devisList = devisRepository.findByStatut(statut);
        return devisMapper.toResponseList(devisList);
    }

    @Transactional(readOnly = true)
    public List<DevisResponse> getDevisExpires() {
        log.info("Récupération des devis expirés");
        List<Devis> devisList = devisRepository.findExpiredDevis(LocalDate.now());
        return devisMapper.toResponseList(devisList);
    }

    @Transactional(readOnly = true)
    public List<DevisResponse> getDevisByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Récupération des devis entre {} et {}", dateDebut, dateFin);
        List<Devis> devisList = devisRepository.findByDateCreationBetween(dateDebut, dateFin);
        return devisMapper.toResponseList(devisList);
    }

    @Transactional
    public void deleteDevis(UUID devisId) {
        log.info("Suppression du devis: {}", devisId);

        if (!devisRepository.existsById(devisId)) {
            throw new IllegalArgumentException("Devis non trouvé: " + devisId);
        }

        devisRepository.deleteById(devisId);

        // Publier l'événement
        devisEventProducer.publishDevisDeleted(devisId);

        log.info("Devis supprimé avec succès: {}", devisId);
    }

    @Transactional
    public DevisResponse accepterDevis(UUID devisId) {
        log.info("Acceptation du devis: {}", devisId);

        Devis devis = devisRepository.findById(devisId)
                .orElseThrow(() -> new IllegalArgumentException("Devis non trouvé: " + devisId));

        devis.setStatut(StatutDevis.ACCEPTE);
        devis.setDateAcceptation(LocalDateTime.now());

        Devis updatedDevis = devisRepository.save(devis);
        DevisResponse response = devisMapper.toResponse(updatedDevis);

        // Publier l'événement
        devisEventProducer.publishDevisAccepted(response);

        log.info("Devis accepté: {}", devisId);
        return response;
    }

    @Transactional
    public DevisResponse refuserDevis(UUID devisId, String motifRefus) {
        log.info("Refus du devis: {}", devisId);

        Devis devis = devisRepository.findById(devisId)
                .orElseThrow(() -> new IllegalArgumentException("Devis non trouvé: " + devisId));

        devis.setStatut(StatutDevis.REFUSE);
        devis.setDateRefus(LocalDateTime.now());
        devis.setMotifRefus(motifRefus);

        Devis updatedDevis = devisRepository.save(devis);

        log.info("Devis refusé: {}", devisId);
        return devisMapper.toResponse(updatedDevis);
    }

    private void calculateMontants(Devis devis) {
        if (devis.getLignesDevis() == null || devis.getLignesDevis().isEmpty()) {
            return;
        }

        BigDecimal montantHT = devis.getLignesDevis().stream()
                .filter(ligne -> !Boolean.TRUE.equals(ligne.getIsTaxLine()))
                .map(ligne -> ligne.getMontantTotal() != null ? ligne.getMontantTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTVA = devis.getLignesDevis().stream()
                .filter(ligne -> Boolean.TRUE.equals(ligne.getIsTaxLine()))
                .map(ligne -> ligne.getMontantTotal() != null ? ligne.getMontantTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTTC = montantHT.add(montantTVA);

        // Appliquer la remise globale si elle existe
        if (devis.getRemiseGlobaleMontant() != null && devis.getRemiseGlobaleMontant().compareTo(BigDecimal.ZERO) > 0) {
            montantTTC = montantTTC.subtract(devis.getRemiseGlobaleMontant());
        }

        devis.setMontantHT(montantHT);
        devis.setMontantTVA(montantTVA);
        devis.setMontantTTC(montantTTC);
        devis.setMontantTotal(montantTTC);
    }

    private String generateNumeroDevis() {
        return "DEV-" + System.currentTimeMillis();
    }
}
