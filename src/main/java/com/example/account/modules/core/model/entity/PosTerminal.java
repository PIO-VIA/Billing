package com.example.account.modules.core.model.entity;

import com.example.account.modules.core.model.enums.PosStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

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
    private String code;

    @Column("status")
    private PosStatus status;

    @Column("agency_id")
    private UUID agencyId;

    @Column("is_active")
    private boolean isActive;
}
