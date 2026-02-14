package com.example.account.modules.core.model.entity;

import com.example.account.modules.core.model.enums.PosStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * POS Terminal entity representing a point-of-sale terminal in an agency.
 * In R2DBC, the Agency relationship is represented by agencyId.
 */
@Table("pos_terminals")
@Getter
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class PosTerminal {

    @Id
    @Column("id")
    private UUID id;

    @Column("code")
    private String code;        // e.g., "POS-01"
   
    @Column("status")
    private PosStatus status;   // OPEN, CLOSED, MAINTENANCE

    /**
     * Reference to the Agency this terminal belongs to.
     * In R2DBC, load the Agency separately via AgencyRepository.
     */
    @Column("agency_id")
    private UUID agencyId;

    @Column("is_active")
    private boolean isActive;
}
