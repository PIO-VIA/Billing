package com.example.account.modules.facturation.service;

import com.example.account.modules.core.context.OrganizationContext;
import com.example.account.modules.facturation.dto.request.BondeReceptionCreateRequest;
import com.example.account.modules.facturation.dto.response.BondeReceptionResponse;
import com.example.account.modules.facturation.mapper.BondeReceptionMapper;
import com.example.account.modules.facturation.model.entity.BondeReception;
import com.example.account.modules.facturation.repository.BonReceptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BonReceptionService {

    private final BonReceptionRepository bonReceptionRepository;
    private final BondeReceptionMapper bondeReceptionMapper;

    @Transactional
    public BondeReceptionResponse createBondeReception(BondeReceptionCreateRequest dto) {
        log.info("Création d'un nouveau Bon de Réception");
        BondeReception bondeReception = bondeReceptionMapper.toEntity(dto);
        //bondeReception.setOrganizationId(OrganizationContext.getCurrentOrganizationId());
        return bondeReceptionMapper.toDto(bonReceptionRepository.save(bondeReception));
    }

    @Transactional(readOnly = true)
    public List<BondeReceptionResponse> getAllBondeReception() {
       // UUID orgId = OrganizationContext.getCurrentOrganizationId();
        List<BondeReception> bons = bonReceptionRepository.findAll();
        return bondeReceptionMapper.toDtoList(bons);
    }

    @Transactional(readOnly = true)
    public BondeReceptionResponse getBondeReceptionById(UUID id) {
        return bonReceptionRepository.findByIdGRNAndOrganizationId(id, OrganizationContext.getCurrentOrganizationId())
                .map(bondeReceptionMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Bon de Réception non trouvé"));
    }

    @Transactional
    public BondeReceptionResponse updateBondeReception(UUID id, BondeReceptionResponse dto) {
        log.info("Mise à jour du Bon de Réception: {}", id);
        BondeReception bondeReception = bonReceptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bon de Réception non trouvé"));
        
        bondeReceptionMapper.updateEntityFromDto(dto, bondeReception);
        return bondeReceptionMapper.toDto(bonReceptionRepository.save(bondeReception));
    }

    @Transactional
    public void deleteBondeReception(UUID id) {
        log.info("Suppression du Bon de Réception: {}", id);
        BondeReception bondeReception = bonReceptionRepository.findByIdGRNAndOrganizationId(id, OrganizationContext.getCurrentOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Bon de Réception non trouvé"));
        bonReceptionRepository.delete(bondeReception);
    }
}
