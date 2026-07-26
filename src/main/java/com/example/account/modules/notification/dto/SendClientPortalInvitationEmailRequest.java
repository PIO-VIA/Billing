package com.example.account.modules.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class SendClientPortalInvitationEmailRequest {

    private UUID organizationId;

    @NotBlank
    private String email;

    private String name;
    private String temporaryPassword;
}
