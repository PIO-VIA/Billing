package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.request.LigneBonLivraisonRequest;
import com.example.account.modules.facturation.dto.response.LigneBonLivraisonResponse;
import com.example.account.modules.facturation.model.entity.LigneBonLivraison;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface LigneBonLivraisonMapper {

    @Mapping(target = "idProduit", source = "idProduit")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "quantite", source = "quantite")
    @Mapping(target = "prixUnitaire", source = "prixUnitaire")
    @Mapping(target = "montant", source = "montant")
    LigneBonLivraison toEntity(LigneBonLivraisonRequest request);

    List<LigneBonLivraison> toEntityList(List<LigneBonLivraisonRequest> requests);

    @Mapping(target = "idProduit", source = "idProduit")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "quantite", source = "quantite")
    @Mapping(target = "prixUnitaire", source = "prixUnitaire")
    @Mapping(target = "montant", source = "montant")
    LigneBonLivraisonResponse toResponse(LigneBonLivraison entity);

    List<LigneBonLivraisonResponse> toResponseList(List<LigneBonLivraison> entities);
}
