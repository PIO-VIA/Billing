package com.example.account.mapper;

import com.example.account.dto.request.BonCommandeCreateRequest;
import com.example.account.dto.request.BonCommandeUpdateRequest;
import com.example.account.dto.response.BonCommandeResponse;
import com.example.account.model.entity.BonCommande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BonCommandeMapper extends BaseMapper<BonCommande, BonCommandeCreateRequest, BonCommandeUpdateRequest, BonCommandeResponse> {

    @Mapping(target = "idBonCommande", expression = "java(generateId())")
    @Mapping(target = "statut", constant = "BROUILLON")
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    @Mapping(target = "validatedAt", ignore = true)
    @Mapping(target = "validatedBy", ignore = true)
    BonCommande toEntity(BonCommandeCreateRequest createRequest);

    @Mapping(target = "idBonCommande", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    @Mapping(target = "validatedAt", ignore = true)
    @Mapping(target = "validatedBy", ignore = true)
    void updateEntityFromRequest(BonCommandeUpdateRequest updateRequest, @MappingTarget BonCommande bonCommande);

    BonCommandeResponse toResponse(BonCommande bonCommande);

    List<BonCommandeResponse> toResponseList(List<BonCommande> bonCommandes);
}
