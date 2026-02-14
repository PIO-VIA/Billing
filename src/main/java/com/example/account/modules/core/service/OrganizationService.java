package com.example.account.modules.core.service;

import com.example.account.modules.core.model.entity.Organization;
import com.example.account.modules.core.model.entity.UserOrganization;
import com.example.account.modules.core.model.enums.OrganizationRole;
import com.example.account.modules.core.repository.OrganizationRepository;
import com.example.account.modules.core.repository.UserOrganizationRepository;
import com.example.account.modules.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Flux<Organization> getAllOrganizations() {
        log.info("Récupération de toutes les organisations");
        return organizationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Mono<Organization> getOrganizationById(UUID id) {
        log.info("Récupération de l'organisation avec l'ID : {}", id);
        return organizationRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée avec l'ID : " + id)));
    }

    @Transactional(readOnly = true)
    public Mono<Organization> getOrganizationByCode(String code) {
        log.info("Récupération de l'organisation avec le code : {}", code);
        return organizationRepository.findByCode(code)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée avec le code : " + code)));
    }

    @Transactional(readOnly = true)
    public Flux<Organization> getUserOrganizations(UUID userId) {
        log.info("Récupération des organisations pour l'utilisateur : {}", userId);
        return userOrganizationRepository.findActiveByUserId(userId)
                .flatMap(uo -> organizationRepository.findById(uo.getOrganizationId()));
    }

    @Transactional
    public Mono<Organization> createOrganization(Organization organization, UUID creatorUserId) {
        log.info("Création d'une nouvelle organisation : {} par l'utilisateur : {}", organization.getShortName(), creatorUserId);
        
        return organizationRepository.existsByCode(organization.getCode())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalStateException("Une organisation avec le code " + organization.getCode() + " existe déjà"));
                    }
                    
                    if (organization.getId() == null) {
                        organization.setId(UUID.randomUUID());
                    }
                    
                    return organizationRepository.save(organization);
                })
                .flatMap(savedOrganization -> {
                    if (creatorUserId == null) {
                        return Mono.just(savedOrganization);
                    }
                    
                    return userRepository.findById(creatorUserId)
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Utilisateur créateur non trouvé avec l'ID : " + creatorUserId)))
                            .flatMap(creator -> {
                                UserOrganization userOrganization = new UserOrganization();
                                userOrganization.setId(UUID.randomUUID());
                                userOrganization.setUserId(creator.getId());
                                userOrganization.setOrganizationId(savedOrganization.getId());
                                userOrganization.setRole(OrganizationRole.OWNER);
                                userOrganization.setIsDefault(true);
                                userOrganization.setIsActive(true);
                                userOrganization.setJoinedAt(LocalDateTime.now());
                                
                                return userOrganizationRepository.save(userOrganization)
                                        .thenReturn(savedOrganization);
                            });
                });
    }

    @Transactional
    public Mono<Organization> createOrganization(Organization organization) {
        return createOrganization(organization, null);
    }

    @Transactional
    public Mono<Organization> updateOrganization(UUID id, Organization organizationDetails) {
        log.info("Mise à jour de l'organisation avec l'ID : {}", id);
        
        return getOrganizationById(id)
                .map(organization -> {
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
                    return organization;
                })
                .flatMap(organizationRepository::save);
    }

    @Transactional
    public Mono<Void> deleteOrganization(UUID id) {
        log.info("Suppression (soft delete) de l'organisation avec l'ID : {}", id);
        return getOrganizationById(id)
                .flatMap(organization -> {
                    organization.setDeletedAt(LocalDateTime.now());
                    organization.setIsActive(false);
                    return organizationRepository.save(organization);
                })
                .then();
    }
}
