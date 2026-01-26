package com.example.account.modules.facturation.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.account.modules.facturation.model.entity.FactureFournisseur;

public interface FactureFournisseurRepository extends JpaRepository<FactureFournisseur,UUID> {

}
