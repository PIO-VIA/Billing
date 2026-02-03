package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.BonLivraisonRequest;
import com.example.account.modules.facturation.dto.response.BonLivraisonResponse;
import com.example.account.modules.facturation.mapper.BonLivraisonMapper;
import com.example.account.modules.facturation.model.entity.BonLivraison;
import com.example.account.modules.facturation.model.entity.LigneBonLivraison;
import com.example.account.modules.facturation.model.enums.StatutBonLivraison;
import com.example.account.modules.facturation.repository.BonLivraisonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonLivraisonService {

    private final BonLivraisonRepository bonLivraisonRepository;
    private final BonLivraisonMapper bonLivraisonMapper;

    @Transactional
    public BonLivraisonResponse createBonLivraison(BonLivraisonRequest request) {
        log.info("Création d'un nouveau bon de livraison pour le client: {}", request.getIdClient());

        BonLivraison bonLivraison = bonLivraisonMapper.toEntity(request);
        

        BonLivraison savedBonLivraison = bonLivraisonRepository.save(bonLivraison);
        return bonLivraisonMapper.toResponse(savedBonLivraison);
    }

    @Transactional(readOnly = true)
    public BonLivraisonResponse getBonLivraisonById(UUID id) {
        log.info("Récupération du bon de livraison: {}", id);
        BonLivraison bonLivraison = bonLivraisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bon de livraison non trouvé: " + id));
        return bonLivraisonMapper.toResponse(bonLivraison);
    }

    @Transactional(readOnly = true)
    public List<BonLivraisonResponse> getAllBonLivraisons() {
        return bonLivraisonMapper.toResponseList(bonLivraisonRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<BonLivraisonResponse> getBonLivraisonsByClient(UUID idClient) {
        return bonLivraisonMapper.toResponseList(bonLivraisonRepository.findByIdClient(idClient));
    }

    @Transactional
    public void deleteBonLivraison(UUID id) {
        if (!bonLivraisonRepository.existsById(id)) {
            throw new IllegalArgumentException("Bon de livraison non trouvé: " + id);
        }
        bonLivraisonRepository.deleteById(id);
    }

    @Transactional
    public BonLivraisonResponse marquerCommeEffectuee(UUID id) {
        log.info("Marquage du bon de livraison {} comme effectuée", id);
        BonLivraison bonLivraison = bonLivraisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bon de livraison non trouvé: " + id));

        if (bonLivraison.getLivraisonEffectuee()) {
            throw new IllegalStateException("La livraison a déjà été effectuée");
        }

        bonLivraison.setLivraisonEffectuee(true);
        bonLivraison.setDateLivraisonEffective(LocalDateTime.now());
        bonLivraison.setStatut(StatutBonLivraison.LIVRE);
        bonLivraison.setUpdatedAt(LocalDateTime.now());

        return bonLivraisonMapper.toResponse(bonLivraisonRepository.save(bonLivraison));
    }

    @Transactional
    public BonLivraisonResponse updateStatut(UUID id, StatutBonLivraison nouveauStatut) {
        log.info("Mise à jour du statut du bon de livraison {} vers {}", id, nouveauStatut);
        BonLivraison bonLivraison = bonLivraisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bon de livraison non trouvé: " + id));

        bonLivraison.setStatut(nouveauStatut);
        bonLivraison.setUpdatedAt(LocalDateTime.now());

        return bonLivraisonMapper.toResponse(bonLivraisonRepository.save(bonLivraison));
    }

     @Transactional
    public BonLivraisonResponse update(UUID id,BonLivraisonRequest request) {
        log.info("Mise à jour du  bon de livraison {} vers {}", id);
        BonLivraison bonLivraison = bonLivraisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bon de livraison non trouvé: " + id));

        bonLivraisonMapper.updateEntityFromDTO(request, bonLivraison);
        
        bonLivraison.setUpdatedAt(LocalDateTime.now());

        return bonLivraisonMapper.toResponse(bonLivraisonRepository.save(bonLivraison));
    }
}
