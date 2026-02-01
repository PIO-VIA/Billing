package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.NoteCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteCreditRepository extends JpaRepository<NoteCredit, UUID> {
    List<NoteCredit> findByOrganizationId(UUID organizationId);
    Optional<NoteCredit> findByNumeroNoteCreditAndOrganizationId(String numeroNoteCredit, UUID organizationId);
    Optional<NoteCredit> findByIdNoteCreditAndOrganizationId(UUID idNoteCredit, UUID organizationId);
}
