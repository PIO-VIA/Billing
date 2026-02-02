package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.request.BonAchatRequest;
import com.example.account.modules.facturation.dto.response.BonAchatResponse;
import com.example.account.modules.facturation.model.entity.BonAchat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BonAchatMapper {

    @Mapping(target = "idBonAchat", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BonAchat toEntity(BonAchatRequest request);

    BonAchatResponse toResponse(BonAchat entity);

    List<BonAchatResponse> toResponseList(List<BonAchat> entities);

    @Mapping(target = "idBonAchat", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(BonAchatRequest request, @MappingTarget BonAchat entity);

}
