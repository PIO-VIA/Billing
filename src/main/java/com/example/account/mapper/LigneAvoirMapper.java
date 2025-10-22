package com.example.account.mapper;

import com.example.account.dto.request.LigneAvoirCreateRequest;
import com.example.account.dto.response.LigneAvoirResponse;
import com.example.account.model.entity.LigneAvoir;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LigneAvoirMapper {

    @Mapping(target = "idLigne", ignore = true)
    LigneAvoir toEntity(LigneAvoirCreateRequest request);

    LigneAvoirResponse toResponse(LigneAvoir ligneAvoir);

    List<LigneAvoir> toEntityList(List<LigneAvoirCreateRequest> requests);

    List<LigneAvoirResponse> toResponseList(List<LigneAvoir> lignesAvoir);
}