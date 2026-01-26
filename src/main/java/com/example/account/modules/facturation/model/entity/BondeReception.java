package com.example.account.modules.facturation.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.example.account.modules.facturation.model.entity.Lines.LineBonReception;
import com.example.account.modules.facturation.model.enums.StatusBonReception;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "goods_receipt_notes")
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor 
@Builder
public class BondeReception {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idGRN;

    @Column(unique = true)
    private String grnNumber;

    // Mapping to Tier (Supplier)
    private UUID supplierId;
    private String supplierName;

    private String transporterCompanyName;
    private String vehicleNumber;

    // Mapping to Purchase Order
    private UUID purchaseOrderId;
    private String purchaseOrderNumber;

    private LocalDate receiptDate;   // Actual date goods received
    private LocalDate documentDate;  // GRN creation date
    private LocalDateTime systemDate;

    @Enumerated(EnumType.STRING)
    private StatusBonReception status;

    /**
     * Storing lines as a JSON column in the database.
     * Requires Hibernate 6+ and a JSON-compatible DB (PostgreSQL, MySQL, etc.)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb") 
    private List<LineBonReception> lines;

    private UUID preparedBy;
    private UUID inspectedBy;
    private UUID approvedBy;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

  

    

    
}