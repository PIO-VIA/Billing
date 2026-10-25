package com.example.account.modules.shared.dto.kernel;

import lombok.Data;
import lombok.NoArgsConstructor;

/** Body of POST /api/employees/invite?organizationId=X — only the fields Billing ever fills in. */
@Data
@NoArgsConstructor
public class KernelInviteEmployeeRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String username;
}
