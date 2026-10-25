package com.example.account.modules.facturation.dto.request;

import com.example.account.modules.facturation.dto.response.ExternalResponses.ProductResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateSaleSizesRequest {

    @NotNull
    private List<ProductResponse.SaleSize> allowedSaleSizes;

    /** Omit to leave the current photo unchanged. */
    private String photo;

    /** Defaults to true if omitted. */
    private Boolean vatApplicable;
}
