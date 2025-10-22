package com.example.account.mapper;

import com.example.account.dto.request.LigneDevisCreateRequest;
import com.example.account.dto.response.LigneDevisResponse;
import com.example.account.model.entity.LigneDevis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LigneDevisMapper {

    @Mapping(target = "idLigne", ignore = true)
    LigneDevis toEntity(LigneDevisCreateRequest request);

    LigneDevisResponse toResponse(LigneDevis ligneDevis);

    List<LigneDevis> toEntityList(List<LigneDevisCreateRequest> requests);

    List<LigneDevisResponse> toResponseList(List<LigneDevis> lignesDevis);
}