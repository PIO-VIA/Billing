package com.example.account.modules.shared.dto.kernel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Kernel's actual GET /api/customers and /api/fournisseurs shape — a generic
 * third-party record (id/code/displayName/roles/accountingAccount/taxNumber),
 * nothing like the old standalone sales-core's own client model. Kernel has
 * no concept of contact details (address/phone/email) or commercial terms
 * (credit limit, running balance) on this record at all — those fields
 * simply don't exist here, they're not just unmapped.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KernelThirdPartyResponse {
    private UUID id;
    // Which Kernel actor this third-party record belongs to — matches the
    // actorId a login response carries, used to resolve "which third party
    // is this logged-in person" for the client/fournisseur portal.
    private UUID partyId;
    // Present on the direct GET /api/third-parties/{id} lookup — a tier-portal
    // login account only ever resolves to one third-party record (see
    // PortalAuthController), so this is how its org gets determined directly,
    // with no org-picker/discover-contexts step needed at all.
    private UUID organizationId;
    private String code;
    private String referenceCode;
    private String displayName;
    private String name;
    private String longName;
    private List<String> roles;
    private Boolean active;
    private Boolean vatSubject;
    private String taxNumber;
    private String legalForm;
    private String accountingAccount;
    private Double authorizedCreditLimit;
}
