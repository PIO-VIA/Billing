package com.example.account.modules.facturation.dto.request.ExternalRequest;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReserveRequest {
    private UUID productId;
    private UUID organizationId;
    private Integer quantity;
    private UUID sellerId;
    private ActionType action; // New Field

    public enum ActionType {
        RESERVE,
        CANCEL
    }
}