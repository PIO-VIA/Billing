package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.entity.Lines.LineFactureFournisseur;
import com.example.account.modules.facturation.model.enums.StatutFactureFournisseur;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table("factures_fournisseur")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class FactureFournisseur extends OrganizationScoped {

    @Id
    @Column("id_facture_fournisseur")
    private UUID idFactureFournisseur;

    @Column("numero_facture")
    private String numeroFacture;

    @Column("id_fournisseur")
    private UUID idFournisseur;

    @Column("nom_fournisseur")
    private String nomFournisseur;

    @Column("adresse_fournisseur")
    private String adresseFournisseur;

    @Column("email_fournisseur")
    private String emailFournisseur;

    @Column("telephone_fournisseur")
    private String telephoneFournisseur;

    @Column("lines")
    private List<LineFactureFournisseur> lines;

    @Column("montant_ht")
    private BigDecimal montantHT;

    @Column("montant_tva")
    private BigDecimal montantTVA;

    @Column("montant_ttc")
    private BigDecimal montantTTC;

    @Column("montant_total")
    private BigDecimal montantTotal;

    @Column("montant_restant")
    private BigDecimal montantRestant;

    @Column("date_facture")
    private LocalDateTime dateFacture;

    @Column("date_echeance")
    private LocalDateTime dateEcheance;

    @Column("statut")
    private StatutFactureFournisseur statut;

    @Column("devise")
    private String devise;

    @Column("notes")
    private String notes;

    @Column("pdf_path")
    private String pdfPath;

    @Column("created_by")
    private UUID createdBy;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}