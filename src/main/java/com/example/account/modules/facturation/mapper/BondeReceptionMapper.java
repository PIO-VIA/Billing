package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.request.BondeReceptionCreateRequest;
import com.example.account.modules.facturation.dto.response.BondeReceptionResponse;
import com.example.account.modules.facturation.model.entity.BondeReception;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BondeReceptionMapper {

    /**
     * Convert Entity to DTO
     */
    BondeReceptionResponse toDto(BondeReception entity);

    /**
     * Convert DTO to Entity
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "version", ignore = true)
    BondeReception toEntity(BondeReceptionResponse dto);

    @Mapping(target = "idGRN", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "version", ignore = true)
    BondeReception toEntity(BondeReceptionCreateRequest dto);

    List<BondeReceptionResponse> toDtoList(List<BondeReception> entities);

    @Mapping(target = "idGRN", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(BondeReceptionResponse dto, @MappingTarget BondeReception entity);
}