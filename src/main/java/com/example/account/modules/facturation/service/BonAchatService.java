package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.BonAchatRequest;
import com.example.account.modules.facturation.dto.response.BonAchatResponse;
import com.example.account.modules.facturation.mapper.BonAchatMapper;
import com.example.account.modules.facturation.model.entity.BonAchat;
import com.example.account.modules.facturation.model.entity.LigneBonAchat;
import com.example.account.modules.facturation.model.enums.StatutBonAchat;
import com.example.account.modules.facturation.repository.BonAchatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonAchatService {

    private final BonAchatRepository bonAchatRepository;
    private final BonAchatMapper bonAchatMapper;

    @Transactional
    public BonAchatResponse createBonAchat(BonAchatRequest request) {
        log.info("Création d'un nouveau bon d'achat pour le fournisseur: {}", request.getIdFournisseur());

        BonAchat bonAchat = bonAchatMapper.toEntity(request);
        
        // Generate number if not provided
        if (bonAchat.getNumeroBonAchat() == null || bonAchat.getNumeroBonAchat().isBlank()) {
            bonAchat.setNumeroBonAchat("BA-" + System.currentTimeMillis());
        }

        // Set status if not provided
        if (bonAchat.getStatut() == null) {
            bonAchat.setStatut(StatutBonAchat.BROUILLON);
        }

        // Link lines
        if (bonAchat.getLignes() != null) {
            for (LigneBonAchat ligne : bonAchat.getLignes()) {
                ligne.setBonAchat(bonAchat);
            }
        }

        BonAchat savedBonAchat = bonAchatRepository.save(bonAchat);
        return bonAchatMapper.toResponse(savedBonAchat);
    }

    @Transactional(readOnly = true)
    public BonAchatResponse getBonAchatById(UUID id) {
        BonAchat bonAchat = bonAchatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bon d'achat non trouvé: " + id));
        return bonAchatMapper.toResponse(bonAchat);
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getAllBonsAchat() {
        return bonAchatMapper.toResponseList(bonAchatRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getBonsAchatByFournisseur(UUID idFournisseur) {
        return bonAchatMapper.toResponseList(bonAchatRepository.findByIdFournisseur(idFournisseur));
    }

    @Transactional
    public void deleteBonAchat(UUID id) {
        if (!bonAchatRepository.existsById(id)) {
            throw new IllegalArgumentException("Bon d'achat non trouvé: " + id);
        }
        bonAchatRepository.deleteById(id);
    }
}
