package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.request.LigneBonAchatRequest;
import com.example.account.modules.facturation.dto.response.LigneBonAchatResponse;
import com.example.account.modules.facturation.model.entity.LigneBonAchat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface LigneBonAchatMapper {

    @Mapping(target = "idLigneBonAchat", ignore = true)
    @Mapping(target = "bonAchat", ignore = true)
    LigneBonAchat toEntity(LigneBonAchatRequest request);

    LigneBonAchatResponse toResponse(LigneBonAchat entity);

    List<LigneBonAchat> toEntityList(List<LigneBonAchatRequest> requests);

    List<LigneBonAchatResponse> toResponseList(List<LigneBonAchat> entities);
}
