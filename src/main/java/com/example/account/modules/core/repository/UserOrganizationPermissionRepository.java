package com.example.account.modules.core.repository;

import com.example.account.modules.core.model.entity.UserOrganizationPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserOrganizationPermissionRepository extends JpaRepository<UserOrganizationPermission, UUID> {
}
