package com.example.account.modules.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/** One organization this account has a customer/supplier record in — used by the frontend to label which org each document row came from. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortalOrganizationOption {
    private UUID organizationId;
    private String organizationName;
    private UUID clientId;
    private List<String> roles;
}
