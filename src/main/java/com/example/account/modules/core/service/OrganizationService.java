package com.example.account.modules.core.service;

import com.example.account.modules.core.model.entity.Organization;
import com.example.account.modules.core.model.entity.User;
import com.example.account.modules.core.model.entity.UserOrganization;
import com.example.account.modules.core.model.enums.OrganizationRole;
import com.example.account.modules.core.repository.OrganizationRepository;
import com.example.account.modules.core.repository.UserOrganizationRepository;
import com.example.account.modules.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Organization> getAllOrganizations() {
        log.info("Récupération de toutes les organisations");
        return organizationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Organization getOrganizationById(UUID id) {
        log.info("Récupération de l'organisation avec l'ID : {}", id);
        return organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organisation non trouvée avec l'ID : " + id));
    }

    @Transactional(readOnly = true)
    public Organization getOrganizationByCode(String code) {
        log.info("Récupération de l'organisation avec le code : {}", code);
        return organizationRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Organisation non trouvée avec le code : " + code));
    }

    @Transactional(readOnly = true)
    public List<Organization> getUserOrganizations(UUID userId) {
        log.info("Récupération des organisations pour l'utilisateur : {}", userId);
        return userOrganizationRepository.findActiveByUserId(userId).stream()
                .map(UserOrganization::getOrganization)
                .collect(Collectors.toList());
    }

    @Transactional
    public Organization createOrganization(Organization organization, UUID creatorUserId) {
        log.info("Création d'une nouvelle organisation : {} par l'utilisateur : {}", organization.getShortName(), creatorUserId);
        
        if (organizationRepository.existsByCode(organization.getCode())) {
            throw new IllegalStateException("Une organisation avec le code " + organization.getCode() + " existe déjà");
        }
        
        Organization savedOrganization = organizationRepository.save(organization);
        
        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur créateur non trouvé avec l'ID : " + creatorUserId));
        
        UserOrganization userOrganization = new UserOrganization();
        userOrganization.setUser(creator);
        userOrganization.setOrganization(savedOrganization);
        userOrganization.setRole(OrganizationRole.OWNER);
        userOrganization.setIsDefault(true); // First org is default
        userOrganization.setIsActive(true);
        
        userOrganizationRepository.save(userOrganization);
        
        return savedOrganization;
    }

    @Transactional
    public Organization createOrganization(Organization organization) {
        // Legacy method or internal creation without specific user
        return createOrganization(organization, null); // CAUTION: Logic might need adjustment if creator is mandatory
    }

    @Transactional
    public Organization updateOrganization(UUID id, Organization organizationDetails) {
        log.info("Mise à jour de l'organisation avec l'ID : {}", id);
        
        Organization organization = getOrganizationById(id);
        
        organization.setShortName(organizationDetails.getShortName());
        organization.setLongName(organizationDetails.getLongName());
        organization.setDescription(organizationDetails.getDescription());
        organization.setLogo(organizationDetails.getLogo());
        organization.setIsActive(organizationDetails.getIsActive());
        organization.setIsIndividual(organizationDetails.getIsIndividual());
        organization.setIsPublic(organizationDetails.getIsPublic());
        organization.setIsBusiness(organizationDetails.getIsBusiness());
        organization.setCountry(organizationDetails.getCountry());
        organization.setCity(organizationDetails.getCity());
        organization.setAddress(organizationDetails.getAddress());
        organization.setLocalization(organizationDetails.getLocalization());
        organization.setOpenTime(organizationDetails.getOpenTime());
        organization.setCloseTime(organizationDetails.getCloseTime());
        organization.setEmail(organizationDetails.getEmail());
        organization.setPhone(organizationDetails.getPhone());
        organization.setWhatsapp(organizationDetails.getWhatsapp());
        organization.setSocialNetworks(organizationDetails.getSocialNetworks());
        organization.setCapitalShare(organizationDetails.getCapitalShare());
        organization.setTaxNumber(organizationDetails.getTaxNumber());
        
        return organizationRepository.save(organization);
    }

    @Transactional
    public void deleteOrganization(UUID id) {
        log.info("Suppression (soft delete) de l'organisation avec l'ID : {}", id);
        Organization organization = getOrganizationById(id);
        organization.setDeletedAt(LocalDateTime.now());
        organization.setIsActive(false);
        organizationRepository.save(organization);
    }
}
