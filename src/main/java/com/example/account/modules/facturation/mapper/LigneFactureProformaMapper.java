package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.request.LigneProformaRequest;
import com.example.account.modules.facturation.dto.response.LigneProformaResponse;
import com.example.account.modules.facturation.model.entity.LigneFactureProforma;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface LigneFactureProformaMapper {

    @Mapping(target = "idLigneProforma", ignore = true)
    @Mapping(target = "factureProforma", ignore = true)
    @Mapping(target = "montantTotal", ignore = true) // Will be calculated in service
    LigneFactureProforma toEntity(LigneProformaRequest request);

    LigneProformaResponse toResponse(LigneFactureProforma entity);

    List<LigneFactureProforma> toEntityList(List<LigneProformaRequest> requests);

    List<LigneProformaResponse> toResponseList(List<LigneFactureProforma> entities);
}
