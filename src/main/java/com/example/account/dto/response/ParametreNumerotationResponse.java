package com.example.account
.dto.response;

import com.example.account
.model.enums.TypeDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametreNumerotationResponse {

    private UUID idParametre;

    private TypeDocument typeDocument;

    private String societeName;

    private String prefixe;

    private String formatDate;

    private Integer nombreChiffres;

    private Integer compteurInitial;

    private Integer compteurActuel;

    private String separateur;

    private Boolean resetAnnuel;

    private Boolean resetMensuel;

    private Integer anneeReference;

    private Integer moisReference;

    private Boolean actif;

    private String description;

    private String exempleNumero;

    private String dernierNumeroGenere;

    private LocalDateTime dateDernierReset;

    private Integer nombreNumerosGeneres;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}