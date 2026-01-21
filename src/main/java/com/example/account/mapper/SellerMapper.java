package com.example.account.mapper;

import com.example.account.dto.request.SellerCreateRequest;
import com.example.account.dto.request.SellerUpdateRequest;
import com.example.account.dto.response.UpdatedSellerResponse;
import com.example.account.model.entity.UserOrganization;
import com.example.account.model.enums.Permission;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Seller-related DTOs.
 * Handles conversion between UserOrganization entities and Seller DTOs.
 */
@Mapper(componentModel = "spring")
@Component
public interface SellerMapper {

    /**
     * Convert UserOrganization entity to UpdatedSellerResponse DTO.
     * This mapper creates the response matching the frontend TypeScript interface.
     *
     * @param userOrganization The user-organization entity (seller)
     * @return Seller response DTO
     */
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "agency", target = "agency")
    @Mapping(source = "salePoint", target = "salePoint")
    @Mapping(target = "Permissions", expression = "java(getActivePermissions(userOrganization))")
    @Mapping(target = "permittedSaleSizes", expression = "java(userOrganization.getPermittedSaleSizesList())")
    UpdatedSellerResponse toSellerResponse(UserOrganization userOrganization);

    /**
     * Helper method to extract active permissions from UserOrganization.
     *
     * @param userOrganization The user-organization entity
     * @return List of active permissions
     */
    default List<Permission> getActivePermissions(UserOrganization userOrganization) {
        return userOrganization.getActivePermissions().stream()
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of UserOrganization entities to seller responses.
     *
     * @param userOrganizations List of user-organization entities
     * @return List of seller response DTOs
     */
    List<UpdatedSellerResponse> toSellerResponseList(List<UserOrganization> userOrganizations);
}
