package com.example.account.dto.request;

import com.example.account.model.enums.Permission;
import com.example.account.model.enums.SaleSize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a new seller.
 * Associates a user with an organization in the SELLER role
 * and assigns permissions and sale sizes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerCreateRequest {
    
    /**
     * ID of the user to be assigned as seller.
     * User must already exist in the system.
     */
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    /**
     * ID of the organization where the seller will operate.
     */
    @NotNull(message = "Organization ID is required")
    private UUID organizationId;
    
    /**
     * Agency/branch where this seller operates.
     */
    @NotBlank(message = "Agency is required")
    private String agency;
    
    /**
     * Sale point/cash register identifier.
     */
    @NotBlank(message = "Sale point is required")
    private String salePoint;
    
    /**
     * List of permissions to grant to this seller.
     * If not provided, default seller permissions will be assigned.
     */
    private List<Permission> permissions;
    
    /**
     * List of permitted sale sizes for this seller.
     * Determines which types of sales the seller can handle.
     */
    private List<SaleSize> permittedSaleSizes;
}
