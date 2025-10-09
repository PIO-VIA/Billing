package com.example.account
.mapper;

import com.example.account
.dto.request.ProduitVenteCreateRequest;
import com.example.account
.dto.request.ProduitVenteUpdateRequest;
import com.example.account
.dto.response.ProduitVenteResponse;
import com.example.account
.model.entity.ProduitVente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProduitVenteMapper extends BaseMapper<ProduitVente, ProduitVenteCreateRequest, ProduitVenteUpdateRequest, ProduitVenteResponse> {

    @Mapping(target = "idProduit", expression = "java(generateId())")
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    ProduitVente toEntity(ProduitVenteCreateRequest createRequest);

    @Mapping(target = "idProduit", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    void updateEntityFromRequest(ProduitVenteUpdateRequest updateRequest, @MappingTarget ProduitVente produitVente);

    ProduitVenteResponse toResponse(ProduitVente produitVente);

    List<ProduitVenteResponse> toResponseList(List<ProduitVente> produitsVente);
}