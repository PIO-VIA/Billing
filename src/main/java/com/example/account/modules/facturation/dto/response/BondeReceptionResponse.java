package com.example.account.modules.facturation.dto.response;


import com.example.account.modules.facturation.model.entity.Lines.LineBonReception;
import com.example.account.modules.facturation.model.enums.StatusBonReception;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BondeReceptionResponse {

    private UUID idGRN;
    private String grnNumber;
    private UUID supplierId;
    private String supplierName;
    private String transporterCompanyName;
    private String vehicleNumber;
    private UUID purchaseOrderId;
    private String purchaseOrderNumber;
    private LocalDate receiptDate;
    private LocalDate documentDate;
    private LocalDateTime systemDate;
    private StatusBonReception status;
    private List<LineBonReception> lines;
    private UUID preparedBy;
    private UUID inspectedBy;
    private UUID approvedBy;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}