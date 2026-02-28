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
    // DTOs now use the same field names as the entity, so MapStruct can map directly.
    BondeReceptionResponse toDto(BondeReception entity);

    @Mapping(target = "updatedAt", ignore = true)
    BondeReception toEntity(BondeReceptionResponse dto);

    @Mapping(target = "updatedAt", ignore = true)
    BondeReception toEntity(BondeReceptionCreateRequest dto);

    List<BondeReceptionResponse> toDtoList(List<BondeReception> entities);

    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(BondeReceptionResponse dto, @MappingTarget BondeReception entity);
}