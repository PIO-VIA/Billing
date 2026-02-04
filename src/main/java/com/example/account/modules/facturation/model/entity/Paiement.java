package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.TypePaiement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("paiements")
public class Paiement extends OrganizationScoped {

    @Id
    @Column("id_paiement")
    private UUID idPaiement;

    @Column("id_client")
    private UUID idClient;

    @Column("montant")
    private BigDecimal montant;

    @Column("date")
    private LocalDate date;

    @Column("journal")
    private String journal;

    @Column("mode_paiement")
    private TypePaiement modePaiement;

    @Column("compte_bancaire_f")
    private String compteBancaireF;

    @Column("memo")
    private String memo;

    @Column("id_facture")
    private UUID idFacture;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
