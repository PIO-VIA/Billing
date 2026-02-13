package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Devis;
import com.example.account.modules.facturation.model.enums.StatutDevis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DevisRepository extends JpaRepository<Devis, UUID> {

    Optional<Devis> findByIdDevis(UUID idDevis);

    Optional<Devis> findByNumeroDevis(String numeroDevis);

    @Query("SELECT d FROM Devis d WHERE d.idClient = ?1")
    List<Devis> findByIdClient(UUID idClient);

    @Query("SELECT d FROM Devis d WHERE d.statut = ?1")
    List<Devis> findByStatut(StatutDevis statut);

    @Query("SELECT d FROM Devis d WHERE d.dateValidite < ?1 AND (d.statut = ENVOYE OR d.statut = BROUILLON)")
    List<Devis> findExpiredDevis(LocalDate currentDate);

    @Query("SELECT d FROM Devis d WHERE d.dateCreation BETWEEN ?1 AND ?2")
    List<Devis> findByDateCreationBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT d FROM Devis d WHERE d.statut = ?1 AND d.dateCreation BETWEEN ?2 AND ?3")
    List<Devis> findByStatutAndDateCreationBetween(StatutDevis statut, LocalDate startDate, LocalDate endDate);

    @Query("SELECT d FROM Devis d WHERE d.idClient = ?1 AND d.statut = ?2")
    List<Devis> findByIdClientAndStatut(UUID idClient, StatutDevis statut);

    @Query("SELECT d FROM Devis d WHERE d.envoyeParEmail = true")
    List<Devis> findSentByEmail();

    @Query("SELECT d FROM Devis d WHERE d.idFactureConvertie IS NOT NULL")
    List<Devis> findConvertedToInvoice();

    Page<Devis> findAll(Pageable pageable);
    List<Devis> findByOrganizationId(UUID organizationId);
}
