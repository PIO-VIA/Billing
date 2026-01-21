package com.example.account.mapper;

import com.example.account.dto.request.FactureCreateRequest;
import com.example.account.dto.request.FactureUpdateRequest;
import com.example.account.dto.response.FactureResponse;
import com.example.account.model.entity.Facture;
import com.example.account.model.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Context;

import java.math.BigDecimal;
import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {LigneFactureMapper.class, ClientMapper.class, PaiementMapper.class}
)
public interface FactureMapper extends BaseMapper<Facture, FactureCreateRequest, FactureUpdateRequest, FactureResponse> {

   
    @Mapping(target = "numeroFacture", expression = "java(generateNumeroFacture())")
    @Mapping(target = "montantTotal", constant = "0")
    @Mapping(target = "montantRestant", constant = "0")
    @Mapping(target = "nomClient", ignore = true)
    @Mapping(target = "adresseClient", ignore = true)
    @Mapping(target = "emailClient", ignore = true)
    @Mapping(target = "telephoneClient", ignore = true)
    @Mapping(target = "montantHT", constant = "0")
    @Mapping(target = "montantTVA", constant = "0")
    @Mapping(target = "montantTTC", constant = "0")
    @Mapping(target = "pdfPath", ignore = true)
    @Mapping(target = "envoyeParEmail", constant = "false")
    @Mapping(target = "dateEnvoiEmail", ignore = true)
    @Mapping(target = "createdBy", ignore = true) // Set by service layer
    @Mapping(target = "validatedBy", ignore = true)
    @Mapping(target = "validatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Facture toEntity(FactureCreateRequest createRequest);

    @Mapping(target = "idFacture", ignore = true)
    @Mapping(target = "numeroFacture", ignore = true)
    @Mapping(target = "montantTotal", ignore = true)
    @Mapping(target = "montantRestant", ignore = true)
    @Mapping(target = "nomClient", ignore = true)
    @Mapping(target = "adresseClient", ignore = true)
    @Mapping(target = "emailClient", ignore = true)
    @Mapping(target = "telephoneClient", ignore = true)
    @Mapping(target = "montantHT", ignore = true)
    @Mapping(target = "montantTVA", ignore = true)
    @Mapping(target = "montantTTC", ignore = true)
    @Mapping(target = "pdfPath", ignore = true)
    @Mapping(target = "envoyeParEmail", ignore = true)
    @Mapping(target = "dateEnvoiEmail", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "validatedBy", ignore = true)
    @Mapping(target = "validatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(FactureUpdateRequest updateRequest, @MappingTarget Facture facture);


    @Mapping(target = "createdByUsername", ignore = true) // Set by service layer if needed
    @Mapping(target = "validatedByUsername", ignore = true) // Set by service layer if needed
    FactureResponse toResponse(Facture facture);

    List<FactureResponse> toResponseList(List<Facture> factures);

    // Méthode utilitaire pour générer le numéro de facture
    default String generateNumeroFacture() {
        return "FAC-" + System.currentTimeMillis();
    }

   
}