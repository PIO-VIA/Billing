package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.FactureCreateRequest;
import com.example.account.modules.facturation.dto.request.FactureUpdateRequest;
import com.example.account.modules.facturation.dto.response.FactureResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import com.example.account.modules.facturation.mapper.FactureMapper;


import com.example.account.modules.facturation.model.entity.Facture;
import com.example.account.modules.facturation.model.enums.StatutFacture;
import com.example.account.modules.tiers.repository.ClientRepository;
import com.example.account.modules.facturation.repository.FactureRepository;
import com.example.account.modules.facturation.service.ExternalServices.*;
import com.example.account.modules.facturation.service.producer.FactureEventProducer;

import com.example.account.modules.facturation.repository.DevisRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class FactureService {

    private static final Logger log = LoggerFactory.getLogger(FactureService.class);

    
    private final FactureRepository factureRepository;
    
    private final FactureMapper factureMapper;
    private final FactureEventProducer factureEventProducer;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;
    private final DevisRepository devisRepository;
    private final AccountingService accountingService;

    private final SellerService sellerService;


    @Transactional
    public FactureResponse createFacture(FactureCreateRequest request) {
        log.info("Création d'une nouvelle facture pour le client: {}", request.getIdClient());

      System.out.println(request);

        // Créer la facture
        Facture facture = factureMapper.toEntity(request);

        

        System.out.println(facture);

        Facture savedFacture = factureRepository.save(facture);

        System.out.println(savedFacture);
        FactureResponse response = factureMapper.toResponse(savedFacture);

        System.out.println(response);

        // Publier l'événement
       // factureEventProducer.publishFactureCreated(response);

        log.info("Facture créée avec succès: {}", savedFacture.getNumeroFacture());
        return response;
    }

    @Transactional
    public FactureResponse updateFacture(UUID factureId, FactureCreateRequest request) {
        log.info("Mise à jour de la facture: {}", factureId);

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée: " + factureId));

        factureMapper.updateEntityFromRequest(request, facture);

        

        Facture updatedFacture = factureRepository.save(facture);
        FactureResponse response = factureMapper.toResponse(updatedFacture);

        // Publier l'événement
      //  factureEventProducer.publishFactureUpdated(response);

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
    public void accountFacture(UUID factureId) throws  Exception{

      

      try {
    accountingService.sendFactureData(factureId);
} catch (Exception e) {
    log.error("Failed to sync facture {} with accounting: {}", factureId, e.getMessage());
    throw new Exception("Accounting sync failed: " + e.getMessage());
}
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

 


    

    @Transactional(readOnly = true)
    public Long countByEtat(StatutFacture etat) {
        return factureRepository.countByEtat(etat);
    }

    /**
     * Génère le PDF d'une facture
     */
   

    /**
     * Envoie un rappel de paiement pour une facture
     */
    @Transactional
    public void envoyerRappelPaiement(UUID factureId) {
        log.info("Envoi d'un rappel de paiement pour la facture: {}", factureId);

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée: " + factureId));

        if (facture.getEmailClient() == null || facture.getEmailClient().isEmpty()) {
            throw new IllegalArgumentException("Le client n'a pas d'adresse email");
        }

        // Envoyer l'email de rappel
        emailService.sendRappelPaiementEmail(facture, facture.getEmailClient());

        log.info("Rappel de paiement envoyé pour la facture {} à {}", facture.getNumeroFacture(), facture.getEmailClient());
    }


    //graphql logic

    //first link objects together
    public void enrichFactures(UUID orgId){
        List<SellerAuthResponse> sellers=sellerService.getSellersByOrganization(orgId);
        //load factures for that organization

    }
}
