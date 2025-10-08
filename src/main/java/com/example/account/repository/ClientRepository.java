package com.example.account.repository;

import com.example.account.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByUsername(String username);
    Optional<Client> findByEmail(String email);

    Optional<Client> findByCodeClient(String codeClient);

}
