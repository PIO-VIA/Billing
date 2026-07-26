package com.example.account.modules.shared.dto.kernel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** One organization inside a discover-contexts result — Kernel's UserOrganizationAccessResponse shape. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KernelOrganizationAccessResponse {
    private UUID organizationId;
    private String organizationCode;
    private String shortName;
    private String longName;
    private String displayName;
    private String legalName;
}
