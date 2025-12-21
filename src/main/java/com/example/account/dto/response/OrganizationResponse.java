package com.example.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {

    private UUID id;
    private String code;
    private String shortName;
    private String longName;
    private String description;
    private String logo;
    private Boolean isActive;
    private Boolean isIndividual;
    private Boolean isPublic;
    private Boolean isBusiness;
    private String country;
    private String city;
    private String address;
    private String localization;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String email;
    private String phone;
    private String whatsapp;
    private String socialNetworks;
    private Double capitalShare;
    private String taxNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
