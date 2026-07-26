package com.example.account.modules.notification.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateDelayRequest {

    @Positive
    private long delayMinutes;
}
