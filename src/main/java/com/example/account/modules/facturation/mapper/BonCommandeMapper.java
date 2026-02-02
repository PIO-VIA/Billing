package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.request.BonCommandeCreateRequest;

import com.example.account.modules.facturation.dto.response.BonCommandeResponse;
import com.example.account.modules.facturation.model.entity.BonCommande;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BonCommandeMapper {

    /**
     * Convertit la requête de création en Entité.
     * Les champs manquants (id, createdAt, etc.) sont gérés par JPA (@GeneratedValue, @PrePersist).
     */
    @Mapping(target = "idBonCommande", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    
    BonCommande toEntity(BonCommandeCreateRequest request);

    /**
     * Met à jour une entité existante à partir d'une requête.
     * Utile pour les méthodes PUT.
     */
    @Mapping(target = "idBonCommande", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    void updateEntityFromRequest(BonCommandeCreateRequest request, @MappingTarget BonCommande entity);

    /**
     * Convertit l'Entité en Réponse DTO pour l'API.
     */
    BonCommandeResponse toResponse(BonCommande entity);

    /**
     * Convertit une liste d'entités en liste de réponses.
     */
    List<BonCommandeResponse> toResponseList(List<BonCommande> entities);
}