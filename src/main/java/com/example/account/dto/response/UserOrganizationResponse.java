package com.example.account.dto.response;

import com.example.account.model.enums.OrganizationRole;
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
    private UUID organizationId;
    private String organizationCode;
    private String organizationName;
    private OrganizationRole role;
    private Boolean isDefault;
    private Boolean isActive;
    private LocalDateTime joinedAt;
}
