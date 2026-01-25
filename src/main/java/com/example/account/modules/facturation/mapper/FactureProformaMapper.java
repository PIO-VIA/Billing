package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.request.ProformaInvoiceRequest;
import com.example.account.modules.facturation.dto.response.ProformaInvoiceResponse;
import com.example.account.modules.facturation.model.entity.FactureProforma;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {LigneFactureProformaMapper.class}
)
public interface FactureProformaMapper {

    @Mapping(target = "idProformaInvoice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lignesFactureProforma", source = "lignes")
    FactureProforma toEntity(ProformaInvoiceRequest request);

    @Mapping(target = "lignes", source = "lignesFactureProforma")
    ProformaInvoiceResponse toResponse(FactureProforma entity);

    List<ProformaInvoiceResponse> toResponseList(List<FactureProforma> entities);
}
