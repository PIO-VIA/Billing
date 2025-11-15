package com.example.account.mapper;

import com.example.account.dto.request.DeviseCreateRequest;
import com.example.account.dto.request.DeviseUpdateRequest;
import com.example.account.dto.response.DeviseResponse;
import com.example.account.model.entity.Devise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DeviseMapper extends BaseMapper<Devise, DeviseCreateRequest, DeviseUpdateRequest, DeviseResponse> {

    @Mapping(target = "idDevise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Devise toEntity(DeviseCreateRequest createRequest);

    @Mapping(target = "idDevise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(DeviseUpdateRequest updateRequest, @MappingTarget Devise devise);

    DeviseResponse toResponse(Devise devise);

    List<DeviseResponse> toResponseList(List<Devise> devises);
}