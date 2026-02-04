package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Devis;
import com.example.account.modules.facturation.model.enums.StatutDevis;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface DevisRepository extends R2dbcRepository<Devis, UUID> {

    Mono<Devis> findByIdDevis(UUID idDevis);

    Mono<Devis> findByNumeroDevis(String numeroDevis);

    @Query("SELECT * FROM devis WHERE id_client::text = :idClient")
    Flux<Devis> findByIdClient(UUID idClient);

    @Query("SELECT * FROM devis WHERE statut = :statut")
    Flux<Devis> findByStatut(StatutDevis statut);

    @Query("SELECT * FROM devis WHERE date_validite < :currentDate AND (statut = 'ENVOYE' OR statut = 'BROUILLON')")
    Flux<Devis> findExpiredDevis(LocalDate currentDate);

    @Query("SELECT * FROM devis WHERE date_creation BETWEEN :startDate AND :endDate")
    Flux<Devis> findByDateCreationBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM devis WHERE statut = :statut AND date_creation BETWEEN :startDate AND :endDate")
    Flux<Devis> findByStatutAndDateCreationBetween(StatutDevis statut, LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM devis WHERE id_client::text = :idClient AND statut = :statut")
    Flux<Devis> findByIdClientAndStatut(UUID idClient, StatutDevis statut);

    @Query("SELECT * FROM devis WHERE envoye_par_email = true")
    Flux<Devis> findSentByEmail();

    @Query("SELECT * FROM devis WHERE id_facture_convertie IS NOT NULL")
    Flux<Devis> findConvertedToInvoice();

    Flux<Devis> findAllBy(Pageable pageable);
}
