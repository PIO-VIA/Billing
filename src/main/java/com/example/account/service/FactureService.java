package com.example.account.service;

import com.example.account.dto.request.FactureCreateRequest;
import com.example.account.dto.request.FactureUpdateRequest;
import com.example.account.dto.response.FactureResponse;
import com.example.account.mapper.FactureMapper;
import com.example.account.model.entity.Client;
import com.example.account.model.entity.Facture;
import com.example.account.model.enums.StatutFacture;
import com.example.account.repository.ClientRepository;
import com.example.account.repository.FactureRepository;
import com.example.account.service.producer.FactureEventProducer;
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
public class FactureService {

    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final FactureMapper factureMapper;
    private final FactureEventProducer factureEventProducer;

    @Transactional
    public FactureResponse createFacture(FactureCreateRequest request) {
        log.info("Création d'une nouvelle facture pour le client: {}", request.getIdClient());

        // Vérifier que le client existe
        Client client = clientRepository.findById(request.getIdClient())
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé: " + request.getIdClient()));

        // Créer la facture
        Facture facture = factureMapper.toEntity(request);

        // Copier les informations du client
        facture.setNomClient(client.getUsername());
        facture.setAdresseClient(client.getAdresse());
        facture.setEmailClient(client.getEmail());
        facture.setTelephoneClient(client.getTelephone());

        // Calculer les montants si des lignes de facture sont fournies
        if (request.getLignesFacture() != null && !request.getLignesFacture().isEmpty()) {
            calculateMontants(facture);
        }

        Facture savedFacture = factureRepository.save(facture);
        FactureResponse response = factureMapper.toResponse(savedFacture);

        // Publier l'événement
        factureEventProducer.publishFactureCreated(response);

        log.info("Facture créée avec succès: {}", savedFacture.getNumeroFacture());
        return response;
    }

    @Transactional
    public FactureResponse updateFacture(UUID factureId, FactureUpdateRequest request) {
        log.info("Mise à jour de la facture: {}", factureId);

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée: " + factureId));

        factureMapper.updateEntityFromRequest(request, facture);

        // Recalculer les montants si nécessaire
        if (facture.getLignesFacture() != null && !facture.getLignesFacture().isEmpty()) {
            calculateMontants(facture);
        }

        Facture updatedFacture = factureRepository.save(facture);
        FactureResponse response = factureMapper.toResponse(updatedFacture);

        // Publier l'événement
        factureEventProducer.publishFactureUpdated(response);

        log.info("Facture mise à jour avec succès: {}", factureId);
        return response;
    }

    @Transactional(readOnly = true)
    public FactureResponse getFactureById(UUID factureId) {
        log.info("Récupération de la facture: {}", factureId);

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée: " + factureId));

        return factureMapper.toResponse(facture);
    }

    @Transactional(readOnly = true)
    public FactureResponse getFactureByNumero(String numeroFacture) {
        log.info("Récupération de la facture par numéro: {}", numeroFacture);

        Facture facture = factureRepository.findByNumeroFacture(numeroFacture)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée avec numéro: " + numeroFacture));

        return factureMapper.toResponse(facture);
    }

    @Transactional(readOnly = true)
    public List<FactureResponse> getAllFactures() {
        log.info("Récupération de toutes les factures");
        List<Facture> factures = factureRepository.findAll();
        return factureMapper.toResponseList(factures);
    }

    @Transactional(readOnly = true)
    public Page<FactureResponse> getAllFactures(Pageable pageable) {
        log.info("Récupération de toutes les factures avec pagination");
        Page<Facture> factures = factureRepository.findAll(pageable);
        return factures.map(factureMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<FactureResponse> getFacturesByClient(UUID clientId) {
        log.info("Récupération des factures du client: {}", clientId);
        List<Facture> factures = factureRepository.findByIdClient(clientId);
        return factureMapper.toResponseList(factures);
    }

    @Transactional(readOnly = true)
    public List<FactureResponse> getFacturesByEtat(StatutFacture etat) {
        log.info("Récupération des factures par état: {}", etat);
        List<Facture> factures = factureRepository.findByEtat(etat);
        return factureMapper.toResponseList(factures);
    }

    @Transactional(readOnly = true)
    public List<FactureResponse> getFacturesEnRetard() {
        log.info("Récupération des factures en retard");
        List<Facture> factures = factureRepository.findOverdueFactures(LocalDate.now());
        return factureMapper.toResponseList(factures);
    }

    @Transactional(readOnly = true)
    public List<FactureResponse> getFacturesNonPayees() {
        log.info("Récupération des factures non payées");
        List<Facture> factures = factureRepository.findUnpaidFactures();
        return factureMapper.toResponseList(factures);
    }

    @Transactional(readOnly = true)
    public List<FactureResponse> getFacturesByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Récupération des factures entre {} et {}", dateDebut, dateFin);
        List<Facture> factures = factureRepository.findByDateFacturationBetween(dateDebut, dateFin);
        return factureMapper.toResponseList(factures);
    }

    @Transactional
    public void deleteFacture(UUID factureId) {
        log.info("Suppression de la facture: {}", factureId);

        if (!factureRepository.existsById(factureId)) {
            throw new IllegalArgumentException("Facture non trouvée: " + factureId);
        }

        factureRepository.deleteById(factureId);

        // Publier l'événement
        factureEventProducer.publishFactureDeleted(factureId);

        log.info("Facture supprimée avec succès: {}", factureId);
    }

    @Transactional
    public FactureResponse marquerCommePaye(UUID factureId) {
        log.info("Marquage de la facture {} comme payée", factureId);

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée: " + factureId));

        facture.setEtat(StatutFacture.PAYE);
        facture.setMontantRestant(BigDecimal.ZERO);

        Facture updatedFacture = factureRepository.save(facture);
        FactureResponse response = factureMapper.toResponse(updatedFacture);

        // Publier l'événement
        factureEventProducer.publishFacturePaid(response);

        log.info("Facture marquée comme payée: {}", factureId);
        return response;
    }

    @Transactional
    public FactureResponse enregistrerPaiement(UUID factureId, BigDecimal montantPaye) {
        log.info("Enregistrement d'un paiement de {} pour la facture {}", montantPaye, factureId);

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée: " + factureId));

        BigDecimal nouveauMontantRestant = facture.getMontantRestant().subtract(montantPaye);

        if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant payé dépasse le montant restant");
        }

        facture.setMontantRestant(nouveauMontantRestant);

        if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) == 0) {
            facture.setEtat(StatutFacture.PAYE);
        } else {
            facture.setEtat(StatutFacture.PARTIELLEMENT_PAYE);
        }

        Facture updatedFacture = factureRepository.save(facture);
        FactureResponse response = factureMapper.toResponse(updatedFacture);

        // Publier l'événement si la facture est complètement payée
        if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) == 0) {
            factureEventProducer.publishFacturePaid(response);
        }

        return response;
    }

    private void calculateMontants(Facture facture) {
        if (facture.getLignesFacture() == null || facture.getLignesFacture().isEmpty()) {
            return;
        }

        BigDecimal montantHT = facture.getLignesFacture().stream()
                .map(ligne -> ligne.getMontantHT() != null ? ligne.getMontantHT() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTVA = facture.getLignesFacture().stream()
                .map(ligne -> ligne.getMontantTVA() != null ? ligne.getMontantTVA() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTTC = montantHT.add(montantTVA);

        facture.setMontantHT(montantHT);
        facture.setMontantTVA(montantTVA);
        facture.setMontantTTC(montantTTC);
        facture.setMontantTotal(montantTTC);
        facture.setMontantRestant(montantTTC);
    }

    @Transactional(readOnly = true)
    public Long countByEtat(StatutFacture etat) {
        return factureRepository.countByEtat(etat);
    }
}
