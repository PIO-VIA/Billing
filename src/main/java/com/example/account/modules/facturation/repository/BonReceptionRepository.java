package com.example.account.modules.facturation.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.account.modules.facturation.model.entity.BondeReception;

import java.util.List;
import java.util.Optional;



@Repository
public interface BonReceptionRepository extends JpaRepository<BondeReception,UUID> {
    
    Optional<BondeReception> findByIdGRN(UUID idGRN);

}
