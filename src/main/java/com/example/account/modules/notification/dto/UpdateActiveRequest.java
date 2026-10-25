package com.example.account.modules.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateActiveRequest {

    @NotNull
    private Boolean active;
}
