package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.LigneBonAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LigneBonAchatRepository extends JpaRepository<LigneBonAchat, UUID> {
}
