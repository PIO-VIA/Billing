package com.example.account.modules.facturation.dto.response.ExternalResponses;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** One row of this org's Kernel employee roster, for the "add from employee" seller picker. */
@Data
@NoArgsConstructor
public class OrganizationEmployeeResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String jobTitle;
    private String status;
    /** True if this person already has a seller record in this organization. */
    private boolean alreadySeller;
}
