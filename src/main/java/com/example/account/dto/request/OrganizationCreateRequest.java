package com.example.account.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCreateRequest {

    @NotBlank(message = "Organization code is required")
    @Size(min = 2, max = 50, message = "Code must be between 2 and 50 characters")
    private String code;

    @NotBlank(message = "Organization short name is required")
    @Size(max = 100, message = "Short name must not exceed 100 characters")
    private String shortName;

    private String longName;

    private String description;

    private String logo;

    @Builder.Default
    private Boolean isIndividual = false;

    @Builder.Default
    private Boolean isPublic = false;

    @Builder.Default
    private Boolean isBusiness = true;

    private String country;

    private String city;

    private String address;

    private String localization;

    private LocalTime openTime;

    private LocalTime closeTime;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    private String whatsapp;

    private String socialNetworks;

    private Double capitalShare;

    private String taxNumber;
}
