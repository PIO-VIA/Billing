package com.example.account.dto.request;

import com.example.account.model.enums.Permission;
import com.example.account.model.enums.SaleSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for updating an existing seller's information.
 * Allows modification of agency, sale point, permissions, and permitted sale sizes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerUpdateRequest {
    
    /**
     * Updated agency/branch where this seller operates.
     */
    private String agency;
    
    /**
     * Updated sale point/cash register identifier.
     */
    private String salePoint;
    
    /**
     * Updated list of permissions for this seller.
     * If provided, replaces existing permissions completely.
     */
    private List<Permission> permissions;
    
    /**
     * Updated list of permitted sale sizes.
     * If provided, replaces existing permitted sale sizes completely.
     */
    private List<SaleSize> permittedSaleSizes;
    
    /**
     * Whether the seller should be marked as inactive.
     * Setting to false deactivates the seller without deleting the record.
     */
    private Boolean isActive;
}
