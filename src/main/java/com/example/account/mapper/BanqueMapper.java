package com.example.account.mapper;

import com.example.account.dto.request.BanqueCreateRequest;
import com.example.account.dto.request.BanqueUpdateRequest;
import com.example.account.dto.response.BanqueResponse;
import com.example.account.model.entity.Banque;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BanqueMapper extends BaseMapper<Banque, BanqueCreateRequest, BanqueUpdateRequest, BanqueResponse> {

    @Mapping(target = "idBanque", ignore = true)
    Banque toEntity(BanqueCreateRequest createRequest);

    @Mapping(target = "idBanque", ignore = true)
    void updateEntityFromRequest(BanqueUpdateRequest updateRequest, @MappingTarget Banque banque);

    BanqueResponse toResponse(Banque banque);

    List<BanqueResponse> toResponseList(List<Banque> banques);
}