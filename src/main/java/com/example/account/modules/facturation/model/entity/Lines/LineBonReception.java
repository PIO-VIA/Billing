package com.example.account.modules.facturation.model.entity.Lines;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public  class LineBonReception {
        private UUID productId;
        private String description;
        private String uom; // Unit of Measure

        private Double orderedQuantity;
        private Double receivedQuantity;
        private Double acceptedQuantity;
        private Double rejectedQuantity;
        private Double shortQuantity;
        private Double damagedQuantity;
        private Double excessQuantity;

        private Double rate;
        private Double lineAmount;
    }