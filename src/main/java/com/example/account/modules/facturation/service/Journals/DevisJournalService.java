package com.example.account.modules.facturation.service.Journals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.account.modules.facturation.dto.response.LigneDevisResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.EnrichedDevis;
import com.example.account.modules.facturation.dto.response.ExternalResponses.EnrichedDevisResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import com.example.account.modules.facturation.model.entity.Devis;
import com.example.account.modules.facturation.model.entity.LigneDevis;
import com.example.account.modules.facturation.repository.DevisRepository;
import com.example.account.modules.facturation.service.ExternalServices.SellerService;
@Service
public class DevisJournalService {
    @Autowired
    private SellerService sellerService;
    @Autowired
    private DevisRepository devisRepository;  
    
    
     public List<EnrichedDevisResponse> enrichDevis(UUID orgId){
        List<SellerAuthResponse> sellers=sellerService.getSellersByOrganization(orgId);
        List<Devis> devis=devisRepository.findByOrganizationId(orgId);
        List<EnrichedDevisResponse> responses=new ArrayList<>();
        for (Devis devis2 : devis) {
            for (SellerAuthResponse seller: sellers) {
                if(devis2.getCreatedBy().equals(seller.getId())){
                    responses.add(toEnrichedResponse(devis2, seller));
                }
            }
            
        }
        return responses;
        
    }

    public EnrichedDevisResponse toEnrichedResponse(Devis devis, SellerAuthResponse seller) {
    if (devis == null) return null;

    return EnrichedDevisResponse.builder()
            // --- Basic Info ---
            .idDevis(devis.getIdDevis())
            .numeroDevis(devis.getNumeroDevis())
            .dateCreation(devis.getDateCreation())
            .dateValidite(devis.getDateValidite())
            .type(devis.getType())
            .statut(devis.getStatut())
            
            // --- Client Info ---
            .idClient(devis.getIdClient())
            .nomClient(devis.getNomClient())
            .adresseClient(devis.getAdresseClient())
            .emailClient(devis.getEmailClient())
            .telephoneClient(devis.getTelephoneClient())
            
            // --- Financials ---
            .montantTotal(devis.getMontantTotal())
            .montantHT(devis.getMontantHT())
            .montantTVA(devis.getMontantTVA())
            .montantTTC(devis.getMontantTTC())
            .finalAmount(devis.getFinalAmount())
            .devise(devis.getDevise())
            .tauxChange(devis.getTauxChange())
            .remiseGlobalePourcentage(devis.getRemiseGlobalePourcentage())
            .remiseGlobaleMontant(devis.getRemiseGlobaleMontant())
            .applyVat(devis.getApplyVat())
            
            // --- JSON Lines Mapping ---
            .lignesDevis(mapLignesToResponse(devis.getLignesDevis()))
            
            // --- Metadata & Refs ---
            .conditionsPaiement(devis.getConditionsPaiement())
            .notes(devis.getNotes())
            .referenceExterne(devis.getReferenceExterne())
            .pdfPath(devis.getPdfPath())
            .envoyeParEmail(devis.getEnvoyeParEmail())
            .dateEnvoiEmail(devis.getDateEnvoiEmail())
            .dateAcceptation(devis.getDateAcceptation())
            .dateRefus(devis.getDateRefus())
            .motifRefus(devis.getMotifRefus())
            .idFactureConvertie(devis.getIdFactureConvertie())
            .validiteOffreJours(devis.getValiditeOffreJours())
            .dateSysteme(devis.getDateSysteme())
            .modeReglement(devis.getModeReglement())
            .nosRef(devis.getNosRef())
            .vosRef(devis.getVosRef())
            .nbreEcheance(devis.getNbreEcheance())
            .referalClientId(devis.getReferalClientId())
            .createdAt(devis.getCreatedAt())
            .updatedAt(devis.getUpdatedAt())

            // --- Enriched Data from SellerAuthResponse ---
            .organizationId(seller != null ? seller.getOrganizationId() : devis.getOrganizationId())
            .organizationName(seller != null ? seller.getOrganizationName() : null)
            .agencyId(seller != null ? seller.getAgencyId() : null)
            .agencyName(seller != null ? seller.getAgencyName() : null)
            .salesPointId(seller != null ? seller.getSalesPointId() : null)
            .salesPointName(seller!=null?seller.getSalesPointName():null)
            // Note: Your DTO has salesPointName as UUID, check if that was a typo in your class definition
            // If it's meant to be String, use seller.getSalesPointName()
            .build();
}

/**
 * Helper to map internal JSON POJO to Response DTO
 */
private List<LigneDevisResponse> mapLignesToResponse(List<LigneDevis> entities) {
    if (entities == null) return Collections.emptyList();
    return entities.stream()
    .<LigneDevisResponse>map(line -> LigneDevisResponse.builder() // Type hint on the map call
           .idProduit(line.getIdProduit() != null ? UUID.fromString(line.getIdProduit()) : null)
            .nomProduit(line.getNomProduit())
            .quantite(line.getQuantite() != null ? line.getQuantite().intValue() : 0)            .prixUnitaire(line.getPrixUnitaire())
            .montantTotal(line.getMontantTotal())
            .build())
    .collect(Collectors.toList());
             // or .collect(Collectors.toList())
}
    
   
}
