package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.LigneFactureProforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LigneFactureProformaRepository extends JpaRepository<LigneFactureProforma, UUID> {
}
