package com.example.account.modules.core.model.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Agency entity representing a branch or location within an organization.
 */
@Table("agencies")
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor 
@Builder
public class Agency {

    @Id
    @Column("id")
    private UUID id;

    @Column("organization_id")
    private UUID organizationId;
    
    @Column("code")
    private String code;
    
    @Column("name")
    private String name;
    
    @Column("location")
    private String location;
    
    @Column("description")
    private String description;
    
    @Column("timezone")
    private String timezone;
    
    @Column("address")
    private String address;
    
    @Column("owner_id")
    private UUID ownerId;
    
    @Column("manager_id")
    private UUID managerId;
    
    @Column("transferable")
    private boolean transferable;
    
    @Column("is_active")
    private boolean isActive;
    
    @Column("logo_url")
    private String logoUrl;
    
    @Column("logo_id")
    private UUID logoId;
    
    @Column("short_name")
    private String shortName;
    
    @Column("long_name")
    private String longName;
    
    @Column("is_individual_business")
    private boolean isIndividualBusiness;
    
    @Column("is_headquarter")
    private boolean isHeadquarter;
    
    @Column("country")
    private String country;
    
    @Column("city")
    private String city;
    
    @Column("latitude")
    private Double latitude;
    
    @Column("longitude")
    private Double longitude;
    
    @Column("open_time")
    private String openTime;
    
    @Column("close_time")
    private String closeTime;
    
    @Column("phone")
    private String phone;
    
    @Column("email")
    private String email;
    
    @Column("whatsapp")
    private String whatsapp;
    
    @Column("greeting_message")
    private String greetingMessage;
    
    @Column("average_revenue")
    private Double averageRevenue;
    
    @Column("capital_share")
    private Double capitalShare;
    
    @Column("registration_number")
    private String registrationNumber;
    
    @Column("social_network")
    private String socialNetwork;
    
    @Column("tax_number")
    private String taxNumber;

    /**
     * Keywords stored as comma-separated string.
     * In R2DBC, @ElementCollection is not supported.
     */
    @Column("keywords")
    private String keywords;

    @Column("is_public")
    private boolean isPublic;
    
    @Column("is_business")
    private boolean isBusiness;
    
    @Column("total_affiliated_customers")
    private Integer totalAffiliatedCustomers;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}