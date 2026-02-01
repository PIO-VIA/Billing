package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.entity.Lines.LineBonReception;
import com.example.account.modules.facturation.model.enums.StatusBonReception;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "goods_receipt_notes",
    indexes = {
        @Index(name = "idx_grn_org", columnList = "organization_id"),
        @Index(name = "idx_grn_org_number", columnList = "organization_id, grn_number"),
        @Index(name = "idx_grn_org_supplier", columnList = "organization_id, supplier_id")
    }
)
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor 
@Builder
public class BondeReception extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idGRN;

    @Column(name = "grn_number", unique = true)
    private String grnNumber;

    // Mapping to Tier (Supplier)
    @Column(name = "supplier_id")
    private UUID supplierId;
    
    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "transporter_company_name")
    private String transporterCompanyName;
    
    @Column(name = "vehicle_number")
    private String vehicleNumber;

    // Mapping to Purchase Order
    @Column(name = "purchase_order_id")
    private UUID purchaseOrderId;
    
    @Column(name = "purchase_order_number")
    private String purchaseOrderNumber;

    @Column(name = "receipt_date")
    private LocalDate receiptDate;   // Actual date goods received
    
    @Column(name = "document_date")
    private LocalDate documentDate;  // GRN creation date
    
    @Column(name = "system_date")
    private LocalDateTime systemDate;

    @Enumerated(EnumType.STRING)
    private StatusBonReception status;

    /**
     * Storing lines as a JSON column in the database.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lines", columnDefinition = "jsonb") 
    private List<LineBonReception> lines;

    @Column(name = "prepared_by")
    private UUID preparedBy;
    
    @Column(name = "inspected_by")
    private UUID inspectedBy;
    
    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Builder.Default
    private Long version = 0L;
}