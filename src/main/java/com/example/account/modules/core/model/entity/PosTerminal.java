package com.example.account.modules.core.model.entity;



import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.example.account.modules.core.model.enums.PosStatus;

import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "pos_terminals")
@Getter
 @Setter 
 @NoArgsConstructor 
 @AllArgsConstructor 
 @Builder
public class PosTerminal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String code;        // e.g., "POS-01"
   
    
    
    @Enumerated(EnumType.STRING)
    private PosStatus status;   // OPEN, CLOSED, MAINTENANCE

    // Relationship to your Agency
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    
    private boolean isActive;
    
}

