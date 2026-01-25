package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.ProformaInvoiceRequest;
import com.example.account.modules.facturation.dto.response.ProformaInvoiceResponse;
import com.example.account.modules.facturation.mapper.FactureProformaMapper;
import com.example.account.modules.facturation.model.entity.FactureProforma;
import com.example.account.modules.facturation.model.entity.LigneFactureProforma;
import com.example.account.modules.facturation.model.enums.StatutProforma;
import com.example.account.modules.facturation.repository.FactureProformaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FactureProformaService {

    private final FactureProformaRepository proformaRepository;
    private final FactureProformaMapper proformaMapper;

    @Transactional
    public ProformaInvoiceResponse createProforma(ProformaInvoiceRequest request) {
        log.info("Création d'une nouvelle facture proforma pour le client: {}", request.getIdClient());

        FactureProforma proforma = proformaMapper.toEntity(request);
        
        // Generate number if not provided
        if (proforma.getNumeroProformaInvoice() == null || proforma.getNumeroProformaInvoice().isBlank()) {
            proforma.setNumeroProformaInvoice("PRO-" + System.currentTimeMillis());
        }

        proforma.setDateCreation(LocalDateTime.now());

        // Set status if not provided
        if (proforma.getStatut() == null) {
            proforma.setStatut(StatutProforma.BROUILLON);
        }

        // Calculate line totals and link lines
        if (proforma.getLignesFactureProforma() != null) {
            for (LigneFactureProforma ligne : proforma.getLignesFactureProforma()) {
                ligne.setFactureProforma(proforma);
                calculateLigneTotal(ligne);
            }
            calculateProformaTotals(proforma);
        }

        FactureProforma savedProforma = proformaRepository.save(proforma);
        return proformaMapper.toResponse(savedProforma);
    }

    private void calculateLigneTotal(LigneFactureProforma ligne) {
        BigDecimal quantite = new BigDecimal(ligne.getQuantite());
        BigDecimal prixU = ligne.getPrixUnitaire() != null ? ligne.getPrixUnitaire() : BigDecimal.ZERO;
        
        BigDecimal total = prixU.multiply(quantite);
        
        if (ligne.getRemiseMontant() != null && ligne.getRemiseMontant().compareTo(BigDecimal.ZERO) > 0) {
            total = total.subtract(ligne.getRemiseMontant());
        } else if (ligne.getRemisePourcentage() != null && ligne.getRemisePourcentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal remise = total.multiply(ligne.getRemisePourcentage()).divide(new BigDecimal(100));
            total = total.subtract(remise);
        }
        
        ligne.setMontantTotal(total);
    }

    private void calculateProformaTotals(FactureProforma proforma) {
        BigDecimal totalHT = proforma.getLignesFactureProforma().stream()
                .map(l -> l.getMontantTotal() != null ? l.getMontantTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        proforma.setMontantHT(totalHT);
        
        // Simple TVA calculation if applicable
        BigDecimal totalTVA = BigDecimal.ZERO;
        for (LigneFactureProforma ligne : proforma.getLignesFactureProforma()) {
            if (ligne.getTauxTva() != null && ligne.getTauxTva().compareTo(BigDecimal.ZERO) > 0) {
                totalTVA = totalTVA.add(ligne.getMontantTotal().multiply(ligne.getTauxTva()).divide(new BigDecimal(100)));
            }
        }
        
        proforma.setMontantTVA(totalTVA);
        BigDecimal ttc = totalHT.add(totalTVA);
        
        // Global discount
        if (proforma.getRemiseGlobaleMontant() != null && proforma.getRemiseGlobaleMontant().compareTo(BigDecimal.ZERO) > 0) {
            ttc = ttc.subtract(proforma.getRemiseGlobaleMontant());
        } else if (proforma.getRemiseGlobalePourcentage() != null && proforma.getRemiseGlobalePourcentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal remise = ttc.multiply(proforma.getRemiseGlobalePourcentage()).divide(new BigDecimal(100));
            ttc = ttc.subtract(remise);
        }
        
        proforma.setMontantTTC(ttc);
        proforma.setMontantTotal(ttc);
        proforma.setFinalAmount(ttc);
    }

    @Transactional(readOnly = true)
    public ProformaInvoiceResponse getProformaById(UUID id) {
        FactureProforma proforma = proformaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture proforma non trouvée: " + id));
        return proformaMapper.toResponse(proforma);
    }

    @Transactional(readOnly = true)
    public List<ProformaInvoiceResponse> getAllProformas() {
        return proformaMapper.toResponseList(proformaRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<ProformaInvoiceResponse> getProformasByClient(UUID idClient) {
        return proformaMapper.toResponseList(proformaRepository.findByIdClient(idClient));
    }

    @Transactional
    public void deleteProforma(UUID id) {
        if (!proformaRepository.existsById(id)) {
            throw new IllegalArgumentException("Facture proforma non trouvée: " + id);
        }
        proformaRepository.deleteById(id);
    }

    @Transactional
    public ProformaInvoiceResponse updateStatut(UUID id, StatutProforma nouveauStatut) {
        FactureProforma proforma = proformaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture proforma non trouvée: " + id));
        
        proforma.setStatut(nouveauStatut);
        
        if (nouveauStatut == StatutProforma.ACCEPTE) {
            proforma.setDateAcceptation(LocalDateTime.now());
        } else if (nouveauStatut == StatutProforma.REFUSE) {
            proforma.setDateRefus(LocalDateTime.now());
        }
        
        return proformaMapper.toResponse(proformaRepository.save(proforma));
    }
}
