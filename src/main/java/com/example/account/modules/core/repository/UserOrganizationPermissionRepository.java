package com.example.account.modules.core.repository;

import com.example.account.modules.core.model.entity.UserOrganizationPermission;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserOrganizationPermissionRepository extends R2dbcRepository<UserOrganizationPermission, UUID> {
}
