package com.example.account.modules.core.dto;

import com.example.account.modules.core.model.enums.OrganizationRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganizationResponse {
    private UUID id;
    private UUID userId;
    private UUID organizationId;
    private String organizationName;
    private String organizationCode;
    private OrganizationRole role;
    private Boolean isDefault;
    private Boolean isActive;
    private LocalDateTime joinedAt;
}
