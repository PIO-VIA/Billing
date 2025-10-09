package com.example.account.repository;

import com.example.account.model.entity.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {
    Page<Journal> findAll(Pageable pageable);

    Optional<Journal> findByNomJournal(String nomJournal);

    List<Journal> findByType(String type);

    @Query("SELECT j FROM Journal j WHERE j.nomJournal LIKE %?1%")
    List<Journal> findByNomJournalContaining(String nomJournal);

    @Query("SELECT COUNT(j) FROM Journal j WHERE j.type = ?1")
    Long countByType(String type);

    boolean existsByNomJournal(String nomJournal);
}
