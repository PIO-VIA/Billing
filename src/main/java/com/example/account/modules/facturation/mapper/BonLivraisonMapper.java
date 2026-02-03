package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.request.BonLivraisonRequest;
import com.example.account.modules.facturation.dto.response.BonLivraisonResponse;
import com.example.account.modules.facturation.model.entity.BonLivraison;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {LigneBonLivraisonMapper.class}
)
public interface BonLivraisonMapper {

    @Mapping(target = "idBonLivraison", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BonLivraison toEntity(BonLivraisonRequest request);

    @Mapping(target = "totalAmount", source = "montantTotal")
    @Mapping(target = "termsAndConditions", source = "conditionsGenerales")
    @Mapping(target = "purchaseOrderNumber", source = "numeroCommande")
    @Mapping(target = "lines", source = "lignes")
    BonLivraisonResponse toResponse(BonLivraison entity);

    @Mapping(target = "lignes", source = "lines")
    List<BonLivraisonResponse> toResponseList(List<BonLivraison> entities);

    void updateEntityFromDTO(BonLivraisonRequest reuqest,@MappingTarget BonLivraison object);
}
