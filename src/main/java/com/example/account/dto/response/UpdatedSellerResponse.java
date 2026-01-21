package com.example.account.dto.response;

import com.example.account.model.enums.Permission;
import com.example.account.model.enums.SaleSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Seller response DTO matching the frontend TypeScript interface exactly.
 * 
 * Frontend TypeScript interface:
 * export type UpdatedSellerResponse = {
 *   username: string;
 *   agency: string;
 *   salePoint: string;
 *   Permissions: Permission[];
 *   permittedSaleSizes: SaleSize[];
 * };
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatedSellerResponse {
    
    /**
     * Username of the seller.
     */
    private String username;
    
    /**
     * Agency/branch where the seller operates.
     */
    private String agency;
    
    /**
     * Sale point/cash register identifier.
     */
    private String salePoint;
    
    /**
     * List of permissions granted to the seller.
     * Note: Frontend uses 'Permissions' with capital P.
     */
    private List<Permission> Permissions;
    
    /**
     * List of permitted sale sizes for this seller.
     */
    private List<SaleSize> permittedSaleSizes;
}
