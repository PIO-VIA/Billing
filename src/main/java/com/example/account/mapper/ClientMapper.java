package com.example.account
.mapper;

import com.example.account
.dto.request.ClientCreateRequest;
import com.example.account
.dto.request.ClientUpdateRequest;
import com.example.account
.dto.response.ClientResponse;
import com.example.account
.model.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ClientMapper extends BaseMapper<Client, ClientCreateRequest, ClientUpdateRequest, ClientResponse> {

    @Mapping(target = "idClient", expression = "java(generateId())")
    @Mapping(target = "soldeCourant", constant = "0.0")
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    Client toEntity(ClientCreateRequest createRequest);

    @Mapping(target = "idClient", ignore = true)
    @Mapping(target = "soldeCourant", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    void updateEntityFromRequest(ClientUpdateRequest updateRequest, @MappingTarget Client client);

    ClientResponse toResponse(Client client);

    List<ClientResponse> toResponseList(List<Client> clients);
}