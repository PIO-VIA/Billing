package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.BonAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BonAchatRepository extends JpaRepository<BonAchat, UUID> {
    Optional<BonAchat> findByNumeroBonAchat(String numeroBonAchat);
    List<BonAchat> findByIdFournisseur(UUID idFournisseur);
    boolean existsByNumeroBonAchat(String numeroBonAchat);
}
