package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.FactureProforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FactureProformaRepository extends JpaRepository<FactureProforma, UUID> {
    Optional<FactureProforma> findByNumeroProformaInvoice(String numeroProformaInvoice);
    List<FactureProforma> findByIdClient(UUID idClient);
    boolean existsByNumeroProformaInvoice(String numeroProformaInvoice);
}
