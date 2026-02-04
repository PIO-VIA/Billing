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

    @Mapping(target = "idLigneBonLivraison", ignore = true)
    LigneBonLivraison toEntity(LigneBonLivraisonRequest request);

    @Mapping(target = "productId", source = "idProduit")
    @Mapping(target = "quantity", source = "quantite")
    @Mapping(target = "unitPrice", source = "prixUnitaire")
    @Mapping(target = "amount", source = "montant")
    LigneBonLivraisonResponse toResponse(LigneBonLivraison entity);

    List<LigneBonLivraison> toEntityList(List<LigneBonLivraisonRequest> requests);

    List<LigneBonLivraisonResponse> toResponseList(List<LigneBonLivraison> entities);
}
