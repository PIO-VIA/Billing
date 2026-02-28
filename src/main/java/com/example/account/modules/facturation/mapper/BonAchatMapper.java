package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.request.BonAchatRequest;
import com.example.account.modules.facturation.dto.response.BonAchatResponse;
import com.example.account.modules.facturation.model.entity.BonAchat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BonAchatMapper {

    /**
     * Map Request (English DTO) -> Entity (French/DB)
     */
    @Mapping(target = "idBonAchat", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "idFournisseur", source = "supplierId")
    @Mapping(target = "nomFournisseur", source = "supplierName")
    @Mapping(target = "dateAchat", source = "dateBonAchat")
    @Mapping(target = "statut", source = "status")
    @Mapping(target = "lignesBonAchat", source = "lines")
    @Mapping(target = "montantHT", source = "subtotalAmount")
    @Mapping(target = "montantTVA", source = "taxAmount")
    @Mapping(target = "montantTTC", source = "grandTotal")
    @Mapping(target = "notes", source = "remarks")
    // allow client to pass organization ID, though it will usually be injected from context
    @Mapping(target = "organizationId", source = "organizationId")
    BonAchat toEntity(BonAchatRequest request);

    /**
     * Map Entity (French/DB) -> Response (English DTO)
     */
    @Mapping(target = "supplierId", source = "idFournisseur")
    @Mapping(target = "supplierName", source = "nomFournisseur")
    @Mapping(target = "dateBonAchat", source = "dateAchat")
    @Mapping(target = "status", source = "statut")
    @Mapping(target = "lines", source = "lignesBonAchat")
    @Mapping(target = "subtotalAmount", source = "montantHT")
    @Mapping(target = "taxAmount", source = "montantTVA")
    @Mapping(target = "grandTotal", source = "montantTTC")
    @Mapping(target = "remarks", source = "notes")
    @Mapping(target = "organizationId" ,source="organizationId")
    // Fields with identical names (supplierCode, deliveryName, etc.) map automatically
    BonAchatResponse toResponse(BonAchat entity);

    /**
     * Map List of Entities -> List of Responses
     */
    List<BonAchatResponse> toResponseList(List<BonAchat> entities);

    /**
     * Update existing Entity from Request
     */
    @Mapping(target = "idBonAchat", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "organizationId", ignore = true) // Usually fixed after creation
    @Mapping(target = "idFournisseur", source = "supplierId")
    @Mapping(target = "nomFournisseur", source = "supplierName")
    @Mapping(target = "dateAchat", source = "dateBonAchat")
    @Mapping(target = "statut", source = "status")
    @Mapping(target = "lignesBonAchat", source = "lines")
    @Mapping(target = "montantHT", source = "subtotalAmount")
    @Mapping(target = "montantTVA", source = "taxAmount")
    @Mapping(target = "montantTTC", source = "grandTotal")
    @Mapping(target = "notes", source = "remarks")
    void updateEntityFromRequest(BonAchatRequest request, @MappingTarget BonAchat entity);
}