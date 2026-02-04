package com.example.account.modules.facturation.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import com.example.account.modules.facturation.model.entity.FactureFournisseur;

public interface FactureFournisseurRepository extends R2dbcRepository<FactureFournisseur, UUID> {

}
