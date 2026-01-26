package com.example.account.modules.facturation.mapper;


import com.example.account.modules.facturation.dto.request.BondeReceptionCreateRequest;
import com.example.account.modules.facturation.dto.response.BondeReceptionResponse;
import com.example.account.modules.facturation.model.entity.BondeReception;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BondeReceptionMapper {

    /**
     * Convert Entity to DTO
     */
    BondeReceptionResponse toDto(BondeReception entity);


    /**
     * Convert DTO to Entity
     * We ignore audit fields during manual mapping to let JPA handle them
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BondeReception toEntity(BondeReceptionResponse dto);

    
     @Mapping(target = "idGRN", ignore = true) 
      @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BondeReception toEntity(BondeReceptionCreateRequest dto);

    List<BondeReceptionResponse> toDtoList(List<BondeReception> entities);


    @Mapping(target = "idGRN", ignore = true) // Don't allow changing the ID
    @Mapping(target = "createdAt", ignore = true) // Keep original timestamp
    void updateEntityFromDto(BondeReceptionResponse dto, @MappingTarget BondeReception entity);
}