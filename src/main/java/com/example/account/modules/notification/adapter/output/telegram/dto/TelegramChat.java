package com.example.account.modules.notification.adapter.output.telegram.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramChat {
    private long id;
}
