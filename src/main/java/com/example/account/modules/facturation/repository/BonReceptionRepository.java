package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.BondeReception;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BonReceptionRepository extends JpaRepository<BondeReception, UUID> {
    List<BondeReception> findByOrganizationId(UUID organizationId);
    Optional<BondeReception> findByIdGRNAndOrganizationId(UUID idGRN, UUID organizationId);
    Optional<BondeReception> findByGrnNumberAndOrganizationId(String grnNumber, UUID organizationId);
}
