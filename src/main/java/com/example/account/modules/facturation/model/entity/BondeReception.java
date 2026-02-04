package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.entity.Lines.LineBonReception;
import com.example.account.modules.facturation.model.enums.StatusBonReception;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("goods_receipt_notes")
public class BondeReception extends OrganizationScoped {

    @Id
    @Column("id_grn")
    private UUID idGRN;

    @Column("grn_number")
    private String grnNumber;

    // Mapping to Tier (Supplier)
    @Column("supplier_id")
    private UUID supplierId;
    
    @Column("supplier_name")
    private String supplierName;

    @Column("transporter_company_name")
    private String transporterCompanyName;
    
    @Column("vehicle_number")
    private String vehicleNumber;

    // Mapping to Purchase Order
    @Column("purchase_order_id")
    private UUID purchaseOrderId;
    
    @Column("purchase_order_number")
    private String purchaseOrderNumber;

    @Column("receipt_date")
    private LocalDate receiptDate;   // Actual date goods received
    
    @Column("document_date")
    private LocalDate documentDate;  // GRN creation date
    
    @Column("system_date")
    private LocalDateTime systemDate;

    @Column("status")
    private StatusBonReception status;

    @Column("lines") 
    private List<LineBonReception> lines;

    @Column("prepared_by")
    private UUID preparedBy;
    
    @Column("inspected_by")
    private UUID inspectedBy;
    
    @Column("approved_by")
    private UUID approvedBy;

    @Column("remarks")
    private String remarks;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Builder.Default
    private Long version = 0L;
}