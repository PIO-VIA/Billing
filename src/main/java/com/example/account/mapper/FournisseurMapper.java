package com.example.account
.mapper;

import com.example.account
.dto.request.FournisseurCreateRequest;
import com.example.account
.dto.request.FournisseurUpdateRequest;
import com.example.account
.dto.response.FournisseurResponse;
import com.example.account
.model.entity.Fournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FournisseurMapper extends BaseMapper<Fournisseur, FournisseurCreateRequest, FournisseurUpdateRequest, FournisseurResponse> {

    @Mapping(target = "idFournisseur", expression = "java(generateId())")
    @Mapping(target = "soldeCourant", constant = "0.0")
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    Fournisseur toEntity(FournisseurCreateRequest createRequest);

    @Mapping(target = "idFournisseur", ignore = true)
    @Mapping(target = "soldeCourant", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    void updateEntityFromRequest(FournisseurUpdateRequest updateRequest, @MappingTarget Fournisseur fournisseur);

    FournisseurResponse toResponse(Fournisseur fournisseur);

    List<FournisseurResponse> toResponseList(List<Fournisseur> fournisseurs);
}