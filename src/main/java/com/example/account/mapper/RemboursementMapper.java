package com.example.account.mapper;

import com.example.account.dto.request.RemboursementCreateRequest;
import com.example.account.dto.request.RemboursementUpdateRequest;
import com.example.account.dto.response.RemboursementResponse;
import com.example.account.model.entity.Remboursement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RemboursementMapper extends BaseMapper<Remboursement, RemboursementCreateRequest, RemboursementUpdateRequest, RemboursementResponse> {

  
    @Mapping(target = "statut", constant = "EN_ATTENTE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Remboursement toEntity(RemboursementCreateRequest createRequest);

    @Mapping(target = "idRemboursement", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(RemboursementUpdateRequest updateRequest, @MappingTarget Remboursement remboursement);

    RemboursementResponse toResponse(Remboursement remboursement);

    List<RemboursementResponse> toResponseList(List<Remboursement> remboursements);
}