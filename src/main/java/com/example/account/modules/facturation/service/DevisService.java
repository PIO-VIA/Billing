package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.DevisCreateRequest;
import com.example.account.modules.facturation.dto.request.LigneDevisCreateRequest;
import com.example.account.modules.facturation.dto.response.DevisResponse;
import com.example.account.modules.facturation.dto.response.LigneDevisResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import com.example.account.modules.facturation.mapper.DevisMapper;

import com.example.account.modules.tiers.model.entity.Client;
import com.example.account.modules.facturation.model.entity.Devis;
import com.example.account.modules.facturation.model.entity.LigneDevis;
import com.example.account.modules.facturation.model.enums.StatutDevis;
import com.example.account.modules.tiers.repository.ClientRepository;
import com.example.account.modules.facturation.repository.DevisRepository;
import com.example.account.modules.facturation.service.ExternalServices.SellerService;
import com.example.account.modules.facturation.service.producer.DevisEventProducer;
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
    
    private final DevisMapper devisMapper;
    private final DevisEventProducer devisEventProducer;

    private final SellerService sellerService;

    @Transactional
    public DevisResponse createDevis(DevisCreateRequest request) {
        log.info("Création d'un nouveau devis pour le client: {}", request.getIdClient());

        
        // Créer le devis
        System.out.println(request);
        Devis devis = devisMapper.toEntity(request);
        System.out.println(devis);
        devis.setCreatedAt(LocalDateTime.now());
        devis.setUpdatedAt(LocalDateTime.now());

       

        
        Devis savedDevis = devisRepository.save(devis);
        System.out.println(savedDevis);
        DevisResponse response = devisMapper.toResponse(savedDevis);
        System.out.println(response);
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

        //calulat the montants
        
        
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

    


   

    
}
