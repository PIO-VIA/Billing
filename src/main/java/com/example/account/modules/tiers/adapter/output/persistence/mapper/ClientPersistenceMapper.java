package com.example.account.modules.tiers.adapter.output.persistence.mapper;

import com.example.account.modules.tiers.domain.model.Client;
import com.example.account.modules.tiers.adapter.output.persistence.ClientPersistenceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientPersistenceMapper {
    Client toDomain(ClientPersistenceEntity entity);
    ClientPersistenceEntity toEntity(Client domain);
}
