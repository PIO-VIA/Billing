package com.example.account.modules.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class AddContactRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    /** Populated server-side from the caller's org context, not accepted from the request body. */
    private UUID organizationId;
}
