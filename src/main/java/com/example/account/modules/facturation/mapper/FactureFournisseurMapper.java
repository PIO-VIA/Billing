package com.example.account.modules.facturation.mapper;


import com.example.account.modules.facturation.dto.request.FactureFournisseurCreateRequest;
import com.example.account.modules.facturation.dto.response.FactureFournisseurResponse;
import com.example.account.modules.facturation.model.entity.FactureFournisseur;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FactureFournisseurMapper {

    /**
     * Convert Entity to Response DTO
     */
    FactureFournisseurResponse toDto(FactureFournisseur entity);

    /**
     * Convert Response DTO back to Entity
     * Useful for logic that requires manipulating existing objects
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FactureFournisseur toEntity(FactureFournisseurResponse dto);

    /**
     * Convert Create Request DTO to Entity
     */
    @Mapping(target = "idFactureFournisseur", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FactureFournisseur toEntity(FactureFournisseurCreateRequest dto);

    /**
     * Convert a list of Entities to a list of Response DTOs
     */
    List<FactureFournisseurResponse> toDtoList(List<FactureFournisseur> entities);

    /**
     * Update an existing Entity from a DTO (for PUT requests)
     */
    @Mapping(target = "idFactureFournisseur", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true) // Protect the original creator
    void updateEntityFromDto(FactureFournisseurResponse dto, @MappingTarget FactureFournisseur entity);
}