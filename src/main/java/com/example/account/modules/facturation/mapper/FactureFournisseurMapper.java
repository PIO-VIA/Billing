package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.request.FactureFournisseurCreateRequest;
import com.example.account.modules.facturation.dto.response.FactureFournisseurResponse;
import com.example.account.modules.facturation.model.entity.FactureFournisseur;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FactureFournisseurMapper {

    FactureFournisseur toEntity(FactureFournisseurCreateRequest request);

    FactureFournisseurResponse toDto(FactureFournisseur entity);

    List<FactureFournisseurResponse> toDtoList(List<FactureFournisseur> entities);

    void updateEntityFromRequest(FactureFournisseurCreateRequest request, @MappingTarget FactureFournisseur entity);
}