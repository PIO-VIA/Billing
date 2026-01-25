package com.example.account.modules.tiers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedSellerResponse {
    private UUID id;
    private String username;
    private String email;
    private String agency;
    private String salePoint;
    private String permittedSaleSizes;
}
