package com.example.account.repository;

import com.example.account.model.entity.Client;
import com.example.account.model.enums.TypeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

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
