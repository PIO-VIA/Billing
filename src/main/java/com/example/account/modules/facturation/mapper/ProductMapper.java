package com.example.account.modules.facturation.mapper;

import com.example.account.modules.facturation.dto.response.ExternalResponses.ProductResponse;
import com.example.account.modules.facturation.service.ExternalServices.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(source = "idProduit", target = "id")
    @Mapping(source = "nomProduit", target = "name")
    @Mapping(source = "typeProduit", target = "type")
    @Mapping(source = "prixVente", target = "salePrice")
    @Mapping(source = "cout", target = "cost")
    @Mapping(source = "categorie", target = "category")
    @Mapping(source = "codeBarre", target = "barcode")
    // Note: allowedSaleSizes and activePromotions will map automatically 
    // because the class names and field names match exactly now.
    Product toEntity(ProductResponse response);

    @Mapping(source = "id", target = "idProduit")
    @Mapping(source = "name", target = "nomProduit")
    @Mapping(source = "type", target = "typeProduit")
    @Mapping(source = "salePrice", target = "prixVente")
    @Mapping(source = "cost", target = "cout")
    @Mapping(source = "category", target = "categorie")
    @Mapping(source = "barcode", target = "codeBarre")
    ProductResponse toResponse(Product entity);
}