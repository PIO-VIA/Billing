package com.example.account.repository;

import com.example.account.model.entity.FactureAvoir;
import com.example.account.model.enums.StatutAvoir;
import com.example.account.model.enums.TypeAvoir;
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
public interface FactureAvoirRepository extends JpaRepository <FactureAvoir, UUID> {
    Optional<FactureAvoir> findByIdAvoir(UUID idAvoir);

    Optional<FactureAvoir> findByNumeroAvoir(String numeroAvoir);

    @Query("SELECT fa FROM FactureAvoir fa WHERE fa.idClient = ?1")
    List<FactureAvoir> findByIdClient(UUID idClient);

    @Query("SELECT fa FROM FactureAvoir fa WHERE fa.idFactureOrigine = ?1")
    List<FactureAvoir> findByIdFactureOrigine(UUID idFactureOrigine);

    @Query("SELECT fa FROM FactureAvoir fa WHERE fa.statut = ?1")
    List<FactureAvoir> findByStatut(StatutAvoir statut);

    @Query("SELECT fa FROM FactureAvoir fa WHERE fa.typeAvoir = ?1")
    List<FactureAvoir> findByTypeAvoir(TypeAvoir typeAvoir);

    @Query("SELECT fa FROM FactureAvoir fa WHERE fa.dateCreation BETWEEN ?1 AND ?2")
    List<FactureAvoir> findByDateCreationBetween(LocalDate startDate, LocalDate endDate);
    @Query("SELECT fa FROM FactureAvoir fa WHERE fa.statut = com.yooyob.erp.model.enums.StatutAvoir.VALIDE AND fa.montantApplique < fa.montantTotal")
    List<FactureAvoir> findAvoirsNonTotalementAppliques();

    @Query("SELECT fa FROM FactureAvoir fa WHERE fa.approuvePar IS NOT NULL")
    List<FactureAvoir> findAvoirsApprouves();

    Page<FactureAvoir> findAll(Pageable pageable);
    @Query("SELECT fa FROM FactureAvoir fa WHERE fa.idClient = ?1 AND fa.statut = ?2")
    List<FactureAvoir> findByIdClientAndStatut(UUID idClient, StatutAvoir statut);

}
