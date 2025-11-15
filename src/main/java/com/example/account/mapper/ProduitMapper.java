package com.example.account.mapper;

import com.example.account.dto.request.ProduitCreateRequest;
import com.example.account.dto.request.ProduitUpdateRequest;
import com.example.account.dto.response.ProduitResponse;
import com.example.account.model.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProduitMapper extends BaseMapper<Produit, ProduitCreateRequest, ProduitUpdateRequest, ProduitResponse> {

   
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    Produit toEntity(ProduitCreateRequest createRequest);

    @Mapping(target = "idProduit", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ProduitUpdateRequest updateRequest, @MappingTarget Produit produit);

    ProduitResponse toResponse(Produit produit);

    List<ProduitResponse> toResponseList(List<Produit> produits);
}
