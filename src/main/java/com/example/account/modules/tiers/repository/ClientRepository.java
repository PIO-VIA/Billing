package com.example.account.modules.tiers.repository;

import com.example.account.modules.tiers.model.entity.Client;
import com.example.account.modules.tiers.model.enums.TypeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByUsername(String username);
    Optional<Client> findByEmail(String email);
    Optional<Client> findByCodeClient(String codeClient);
    List<Client> findByTypeClient(TypeClient typeClient);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByCodeClient(String codeClient);
    @Query("SELECT c FROM Client c WHERE c.actif = true")
    List<Client> findAllActiveClients();
    @Query("SELECT COUNT(c) FROM Client c WHERE c.actif = true")
    Long countActiveClients();
}
