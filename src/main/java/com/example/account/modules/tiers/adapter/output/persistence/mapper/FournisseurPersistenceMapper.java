package com.example.account.modules.tiers.adapter.output.persistence.mapper;

import com.example.account.modules.tiers.domain.model.Fournisseur;
import com.example.account.modules.tiers.adapter.output.persistence.FournisseurPersistenceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FournisseurPersistenceMapper {
    Fournisseur toDomain(FournisseurPersistenceEntity entity);
    FournisseurPersistenceEntity toEntity(Fournisseur domain);
}
