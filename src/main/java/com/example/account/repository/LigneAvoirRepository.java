package com.example.account.repository;

import com.example.account.model.entity.LigneAvoir;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LigneAvoirRepository extends JpaRepository<LigneAvoir, UUID> {
    Page<LigneAvoir> findAll(Pageable pageable);

    List<LigneAvoir> findByIdAvoir(UUID idAvoir);

}
