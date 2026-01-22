package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Devis;
import com.example.account.modules.facturation.model.entity.LigneDevis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LigneDevisRepository extends JpaRepository<LigneDevis, UUID> {
    Page<LigneDevis> findAll(Pageable pageable);

    List<LigneDevis> findByDevis(Devis devis);

}
