package com.example.account.mapper;

import com.example.account.dto.request.PaiementCreateRequest;
import com.example.account.dto.request.PaiementUpdateRequest;
import com.example.account.dto.response.PaiementResponse;
import com.example.account.model.entity.Paiement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PaiementMapper extends BaseMapper<Paiement, PaiementCreateRequest, PaiementUpdateRequest, PaiementResponse> {

    @Mapping(target = "idPaiement", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Paiement toEntity(PaiementCreateRequest createRequest);

    @Mapping(target = "idPaiement", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(PaiementUpdateRequest updateRequest, @MappingTarget Paiement paiement);

    PaiementResponse toResponse(Paiement paiement);

    List<PaiementResponse> toResponseList(List<Paiement> paiements);
}