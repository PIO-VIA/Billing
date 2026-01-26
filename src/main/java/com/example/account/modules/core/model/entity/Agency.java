package com.example.account.modules.core.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

// ==========================================
// 1. MAIN AGENCY ENTITY
// ==========================================
@Entity
@Table(name = "agencies")
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor 
@Builder
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID organizationId;
    private String code;
    private String name;
    private String location;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String timezone;
    private String address;
    private UUID ownerId;
    private UUID managerId;
    
    private boolean transferable;
    private boolean isActive;
    
    private String logoUrl;
    private UUID logoId;
    private String shortName;
    private String longName;
    
    private boolean isIndividualBusiness;
    private boolean isHeadquarter;
    
    private String country;
    private String city;
    private Double latitude;
    private Double longitude;
    
    private String openTime;
    private String closeTime;
    private String phone;
    private String email;
    private String whatsapp;
    private String greetingMessage;
    
    private Double averageRevenue;
    private Double capitalShare;
    private String registrationNumber;
    private String socialNetwork;
    private String taxNumber;

    @ElementCollection
    @CollectionTable(name = "agency_keywords", joinColumns = @JoinColumn(name = "agency_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    private boolean isPublic;
    private boolean isBusiness;
    private Integer totalAffiliatedCustomers;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

   // @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL, orphanRemoval = true)
   // private List<OpeningHoursRule> openingHoursRules;

  //  @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<SpecialOpeningHours> specialOpeningHours;
}