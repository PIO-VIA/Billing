package com.example.account.mapper;

import com.example.account.dto.request.ProduitCreateRequest;
import com.example.account.dto.request.ProduitUpdateRequest;

import com.example.account.dto.response.ProduitResponse;
import com.example.account.model.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProduitMapper extends BaseMapper<Produit, ProduitCreateRequest, ProduitUpdateRequest, ProduitResponse> {

    @Mapping(target = "idProduit", expression = "java(generateId())")
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    Produit toEntity(ProduitCreateRequest createRequest);

    @Mapping(target = "idProduit", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    void updateEntityFromRequest(ProduitUpdateRequest updateRequest, @MappingTarget Produit produitVente);

    ProduitResponse toResponse(Produit produitVente);

    List<ProduitResponse> toResponseList(List<Produit> produitsVente);
}