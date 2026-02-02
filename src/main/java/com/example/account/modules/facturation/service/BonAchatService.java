package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.BonAchatRequest;
import com.example.account.modules.facturation.dto.response.BonAchatResponse;
import com.example.account.modules.facturation.mapper.BonAchatMapper;
import com.example.account.modules.facturation.model.entity.BonAchat;
import com.example.account.modules.facturation.repository.BonAchatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonAchatService {

    private final BonAchatRepository bonAchatRepository;
    private final BonAchatMapper bonAchatMapper;

    /**
     * POST - Créer un nouveau bon d'achat
     */
    @Transactional
    public BonAchatResponse createBonAchat(BonAchatRequest request) {
        log.info("Création d'un nouveau bon d'achat, numéro: {}", request.getNumeroBonAchat());

        BonAchat bonAchat = bonAchatMapper.toEntity(request);
        
        // Les lignes sont automatiquement mappées et seront persistées en JSON via @JdbcTypeCode
        BonAchat savedBonAchat = bonAchatRepository.save(bonAchat);
        
        log.debug("Bon d'achat sauvegardé avec succès: {}", savedBonAchat.getIdBonAchat());
        return bonAchatMapper.toResponse(savedBonAchat);
    }

    /**
     * PUT - Mettre à jour un bon d'achat existant
     */
    @Transactional
    public BonAchatResponse updateBonAchat(UUID id, BonAchatRequest request) {
        log.info("Mise à jour du bon d'achat ID: {}", id);

        BonAchat existingBonAchat = bonAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon d'achat non trouvé avec l'ID: " + id));

        // Mise à jour des champs via le mapper (MapStruct gère souvent mieux cela avec @MappingTarget)
        // Ici on utilise une approche manuelle pour plus de clarté sur la logique JSON
        bonAchatMapper.updateEntityFromRequest(request, existingBonAchat);

        // Si des recalculs de totaux sont nécessaires avant la sauvegarde, ils se font ici
        
        BonAchat updatedBonAchat = bonAchatRepository.save(existingBonAchat);
        return bonAchatMapper.toResponse(updatedBonAchat);
    }

    @Transactional(readOnly = true)
    public BonAchatResponse getBonAchatById(UUID id) {
        return bonAchatRepository.findById(id)
                .map(bonAchatMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Bon d'achat non trouvé: " + id));
    }

    @Transactional(readOnly = true)
    public List<BonAchatResponse> getAllBonsAchat() {
        return bonAchatMapper.toResponseList(bonAchatRepository.findAll());
    }

   

    @Transactional
    public void deleteBonAchat(UUID id) {
        if (!bonAchatRepository.existsById(id)) {
            throw new IllegalArgumentException("Bon d'achat non trouvé: " + id);
        }
        bonAchatRepository.deleteById(id);
        log.info("Bon d'achat ID: {} supprimé", id);
    }
}