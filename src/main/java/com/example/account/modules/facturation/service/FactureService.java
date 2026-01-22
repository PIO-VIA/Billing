package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.FactureCreateRequest;
import com.example.account.modules.facturation.dto.request.FactureUpdateRequest;
import com.example.account.modules.facturation.dto.response.FactureResponse;
import com.example.account.modules.facturation.mapper.FactureMapper;
import com.example.account.modules.facturation.model.entity.LigneFacture;
import com.example.account.modules.facturation.repository.LigneFactureRepository;
import com.example.account.modules.tiers.model.entity.Client;
import com.example.account.modules.facturation.model.entity.Facture;
import com.example.account.modules.facturation.model.enums.StatutFacture;
import com.example.account.modules.tiers.repository.ClientRepository;
import com.example.account.modules.facturation.repository.FactureRepository;
import com.example.account.modules.facturation.service.producer.FactureEventProducer;
import com.example.account.modules.facturation.model.entity.Devis;
import com.example.account.modules.facturation.model.entity.LigneDevis;
import com.example.account.modules.facturation.model.enums.StatutDevis;
import com.example.account.modules.facturation.repository.DevisRepository;
import com.example.account.modules.facturation.repository.LigneDevisRepository;
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
public class FactureService {

    private final ClientRepository clientRepository;
    private final FactureRepository factureRepository;
    private final LigneFactureRepository ligneFactureRepository;
    private final FactureMapper factureMapper;
    private final FactureEventProducer factureEventProducer;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;
    private final DevisRepository devisRepository;
    private final LigneDevisRepository ligneDevisRepository;


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

    @Transactional
    public FactureResponse createFactureFromDevis(UUID devisId) {
        log.info("Création d'une facture à partir du devis: {}", devisId);

        Devis devis = devisRepository.findById(devisId)
                .orElseThrow(() -> new IllegalArgumentException("Devis non trouvé: " + devisId));

        if (devis.getStatut() == StatutDevis.CONVERTI_EN_FACTURE) {
            throw new IllegalStateException("Ce devis a déjà été facturé");
        }

        // Créer la facture à partir du devis
        Facture facture = new Facture();
        facture.setIdClient(devis.getIdClient());
        facture.setNomClient(devis.getNomClient());
        facture.setAdresseClient(devis.getAdresseClient());
        facture.setEmailClient(devis.getEmailClient());
        facture.setTelephoneClient(devis.getTelephoneClient());
        facture.setConditionsPaiement(devis.getConditionsPaiement());
        facture.setDevise(devis.getDevise());
        facture.setTauxChange(devis.getTauxChange());
        
        // Initialiser les champs spécifiques à la facture
        facture.setNumeroFacture("FACT-" + System.currentTimeMillis()); // Génération temporaire
        facture.setDateFacturation(LocalDate.now());
        facture.setDateEcheance(LocalDate.now().plusDays(30)); // Par défaut 30 jours
        facture.setEtat(StatutFacture.BROUILLON);
        facture.setOrganization(devis.getOrganization());
        
        // Sauvegarder la facture pour avoir un ID
        Facture savedFacture = factureRepository.save(facture);

        // Copier les lignes
        List<LigneDevis> lignesDevis = ligneDevisRepository.findByDevis(devis);
        if (lignesDevis != null && !lignesDevis.isEmpty()) {
            int ordre = 1;
            for (LigneDevis ld : lignesDevis) {
                LigneFacture lf = new LigneFacture();
                lf.setIdFacture(savedFacture.getIdFacture());
                lf.setOrganization(devis.getOrganization());
                lf.setQuantite(ld.getQuantite());
                lf.setDescription(ld.getDescription());
                lf.setDebit(ld.getDebit());
                lf.setCredit(ld.getCredit());
                lf.setIsTaxLine(ld.getIsTaxLine());
                lf.setIdProduit(ld.getIdProduit());
                lf.setNomProduit(ld.getNomProduit());
                lf.setPrixUnitaire(ld.getPrixUnitaire());
                lf.setMontantTotal(ld.getMontantTotal());
                lf.setOrdre(ordre++);
                lf.setCreatedAt(LocalDateTime.now());
                lf.setUpdatedAt(LocalDateTime.now());
                ligneFactureRepository.save(lf);
            }
        }
        
        // Mettre à jour les montants (copie du devis pour l'instant)
        savedFacture.setMontantHT(devis.getMontantHT());
        savedFacture.setMontantTVA(devis.getMontantTVA());
        savedFacture.setMontantTTC(devis.getMontantTTC());
        savedFacture.setMontantTotal(devis.getMontantTotal());
        savedFacture.setMontantRestant(devis.getMontantTotal());

        savedFacture = factureRepository.save(savedFacture);

        // Mettre à jour le devis
        devis.setStatut(StatutDevis.CONVERTI_EN_FACTURE);
        devis.setIdFactureConvertie(savedFacture.getIdFacture());
        devisRepository.save(devis);

        FactureResponse response = factureMapper.toResponse(savedFacture);
        factureEventProducer.publishFactureCreated(response);
        
        return response;
    }


    private void calculateMontants(Facture facture) {
        if (facture.getLignesFacture() == null || facture.getLignesFacture().isEmpty()) {
            return;
        }

        BigDecimal montantHT = facture.getLignesFacture().stream()
                .filter(ligne -> !Boolean.TRUE.equals(ligne.getIsTaxLine()))
                .map(ligne -> ligne.getMontantTotal() != null ? ligne.getMontantTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTVA = facture.getLignesFacture().stream()
                .filter(ligne -> Boolean.TRUE.equals(ligne.getIsTaxLine()))
                .map(ligne -> ligne.getMontantTotal() != null ? ligne.getMontantTotal() : BigDecimal.ZERO)
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

    /**
     * Génère le PDF d'une facture
     */
    @Transactional(readOnly = true)
    public byte[] genererPdfFacture(UUID factureId) {
        log.info("Génération du PDF pour la facture: {}", factureId);

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée: " + factureId));

        Client client = clientRepository.findById(facture.getIdClient())
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé: " + facture.getIdClient()));

        return pdfGeneratorService.generateFacturePdf(facture, client);
    }

    /**
     * Génère et sauvegarde le PDF d'une facture
     */
    @Transactional
    public String genererEtSauvegarderPdfFacture(UUID factureId) {
        log.info("Génération et sauvegarde du PDF pour la facture: {}", factureId);

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée: " + factureId));

        Client client = clientRepository.findById(facture.getIdClient())
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé: " + facture.getIdClient()));

        String pdfPath = pdfGeneratorService.generateAndSaveFacturePdf(facture, client);

        // Mettre à jour le chemin du PDF dans la facture
        facture.setPdfPath(pdfPath);
        factureRepository.save(facture);

        return pdfPath;
    }

    /**
     * Envoie la facture par email au client
     */
    @Transactional
    public void envoyerFactureParEmail(UUID factureId) {
        log.info("Envoi de la facture {} par email", factureId);

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée: " + factureId));

        if (facture.getEmailClient() == null || facture.getEmailClient().isEmpty()) {
            throw new IllegalArgumentException("Le client n'a pas d'adresse email");
        }

        Client client = clientRepository.findById(facture.getIdClient())
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé: " + facture.getIdClient()));

        // Générer le PDF
        byte[] pdfBytes = pdfGeneratorService.generateFacturePdf(facture, client);

        // Envoyer l'email avec le PDF en pièce jointe
        emailService.sendFactureCreationEmail(facture, facture.getEmailClient(), pdfBytes);

        // Mettre à jour le statut d'envoi
        facture.setEnvoyeParEmail(true);
        facture.setDateEnvoiEmail(java.time.LocalDateTime.now());
        factureRepository.save(facture);

        log.info("Facture {} envoyée par email à {}", facture.getNumeroFacture(), facture.getEmailClient());
    }

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
}
