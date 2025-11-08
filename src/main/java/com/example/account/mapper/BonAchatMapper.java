package com.example.account.mapper;

import com.example.account.dto.request.BonAchatCreateRequest;
import com.example.account.dto.request.BonAchatUpdateRequest;
import com.example.account.dto.response.BonAchatResponse;
import com.example.account.model.entity.BonAchat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BonAchatMapper extends BaseMapper<BonAchat, BonAchatCreateRequest, BonAchatUpdateRequest, BonAchatResponse> {

   
    @Mapping(target = "statut", constant = "EN_ATTENTE")
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    @Mapping(target = "validatedAt", ignore = true)
    @Mapping(target = "validatedBy", ignore = true)
    BonAchat toEntity(BonAchatCreateRequest createRequest);

    @Mapping(target = "idBonAchat", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    @Mapping(target = "validatedAt", ignore = true)
    @Mapping(target = "validatedBy", ignore = true)
    void updateEntityFromRequest(BonAchatUpdateRequest updateRequest, @MappingTarget BonAchat bonAchat);

    BonAchatResponse toResponse(BonAchat bonAchat);

    List<BonAchatResponse> toResponseList(List<BonAchat> bonAchats);
}
