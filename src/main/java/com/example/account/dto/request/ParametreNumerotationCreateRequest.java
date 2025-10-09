package com.example.account
.dto.request;

import com.example.account
.model.enums.TypeDocument;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametreNumerotationCreateRequest {

    @NotNull(message = "Le type de document est obligatoire")
    private TypeDocument typeDocument;

    private String societeName;

    @Size(max = 10, message = "Le préfixe ne peut pas dépasser 10 caractères")
    private String prefixe;

    private String formatDate;

    @Min(value = 1, message = "Le nombre de chiffres doit être au minimum de 1")
    private Integer nombreChiffres;

    @Min(value = 1, message = "Le compteur initial doit être au minimum de 1")
    private Integer compteurInitial;

    @Size(max = 5, message = "Le séparateur ne peut pas dépasser 5 caractères")
    private String separateur;

    @Builder.Default
    private Boolean resetAnnuel = false;

    @Builder.Default
    private Boolean resetMensuel = false;

    @Builder.Default
    private Boolean actif = true;

    private String description;

    private String exempleNumero;
}