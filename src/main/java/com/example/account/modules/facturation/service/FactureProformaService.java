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
        
     
        proforma.setDateCreation(LocalDateTime.now());

        // Set status if not provided
        if (proforma.getStatut() == null) {
            proforma.setStatut(StatutProforma.BROUILLON);
        }

        

        FactureProforma savedProforma = proformaRepository.save(proforma);
        return proformaMapper.toResponse(savedProforma);
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


      @Transactional
    public ProformaInvoiceResponse updateFactureProforma(UUID id, ProformaInvoiceRequest request) {
        FactureProforma proforma = proformaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture proforma non trouvée: " + id));
        
        proformaMapper.updateProformaFromDTO(request, proforma);
        
      
        
        return proformaMapper.toResponse(proformaRepository.save(proforma));
    }
}
