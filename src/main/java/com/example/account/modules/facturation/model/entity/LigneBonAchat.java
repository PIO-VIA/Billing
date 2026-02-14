package com.example.account.modules.facturation.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneBonAchat{

   
    
    
    private UUID productId; 
    private String productCode;
    private String productName;
    private String uom;

    private Integer orderedQuantity;
    private BigDecimal unitPrice;
    private Boolean taxable;
    private BigDecimal vatAmount;
    private BigDecimal totalAmount;
}