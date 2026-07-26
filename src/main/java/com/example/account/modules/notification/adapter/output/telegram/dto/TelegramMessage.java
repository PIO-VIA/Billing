package com.example.account.modules.notification.adapter.output.telegram.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramMessage {

    @JsonProperty("message_id")
    private long messageId;

    private TelegramChat chat;
    private String text;
}
