package com.example.account.modules.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ContactResponse {
    private UUID id;
    private String name;
    private String email;
    private UUID organizationId;
    private String status;
    private boolean linked;
    private Instant createdAt;
}
