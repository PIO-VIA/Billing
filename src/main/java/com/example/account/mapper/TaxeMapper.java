package com.example.account.mapper;

import com.example.account.dto.request.TaxeCreateRequest;
import com.example.account.dto.request.TaxeUpdateRequest;
import com.example.account.dto.response.TaxeResponse;
import com.example.account.model.entity.Taxes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaxeMapper extends BaseMapper<Taxes, TaxeCreateRequest, TaxeUpdateRequest, TaxeResponse> {

    @Mapping(target = "idTaxe", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Taxes toEntity(TaxeCreateRequest createRequest);

    @Mapping(target = "idTaxe", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(TaxeUpdateRequest updateRequest, @MappingTarget Taxes taxe);

    TaxeResponse toResponse(Taxes taxe);

    List<TaxeResponse> toResponseList(List<Taxes> taxes);
}