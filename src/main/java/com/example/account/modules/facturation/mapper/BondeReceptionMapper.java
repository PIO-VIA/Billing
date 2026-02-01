package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.request.BondeReceptionCreateRequest;
import com.example.account.modules.facturation.dto.response.BondeReceptionResponse;
import com.example.account.modules.facturation.model.entity.BondeReception;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    builder = @Builder(disableBuilder = true)
)
public interface BondeReceptionMapper {

    BondeReceptionResponse toDto(BondeReception entity);

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