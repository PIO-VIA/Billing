package com.example.account
.mapper;

import com.example.account
.dto.request.EtapeApprobationCreateRequest;
import com.example.account
.dto.response.EtapeApprobationResponse;
import com.example.account
.model.entity.EtapeApprobation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EtapeApprobationMapper {

    EtapeApprobation toEntity(EtapeApprobationCreateRequest request);

    EtapeApprobationResponse toResponse(EtapeApprobation etapeApprobation);

    List<EtapeApprobation> toEntityList(List<EtapeApprobationCreateRequest> requests);

    List<EtapeApprobationResponse> toResponseList(List<EtapeApprobation> etapesApprobation);
}