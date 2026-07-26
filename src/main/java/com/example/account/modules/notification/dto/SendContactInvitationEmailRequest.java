package com.example.account.modules.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class SendContactInvitationEmailRequest {

    private UUID organizationId;

    @NotBlank
    private String email;

    private String name;

    /** Fully-built https://t.me/<bot>?start=<token> link — sales-core owns the bot username/token. */
    @NotBlank
    private String telegramDeepLink;
}
