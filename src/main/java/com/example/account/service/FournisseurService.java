package com.example.account.service;

import com.example.account.dto.request.FournisseurCreateRequest;
import com.example.account.dto.request.FournisseurUpdateRequest;
import com.example.account.dto.response.FournisseurResponse;
import com.example.account.mapper.FournisseurMapper;
import com.example.account.model.entity.Fournisseur;
import com.example.account.repository.FournisseurRepository;
import com.example.account.service.producer.FournisseurEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final FournisseurMapper fournisseurMapper;
    private final FournisseurEventProducer fournisseurEventProducer;

    @Transactional
    public FournisseurResponse createFournisseur(FournisseurCreateRequest request) {
        log.info("Création d'un nouveau fournisseur: {}", request.getUsername());

        // Vérifications
        if (fournisseurRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Un fournisseur avec ce username existe déjà");
        }
        if (request.getEmail() != null && fournisseurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un fournisseur avec cet email existe déjà");
        }

        // Créer et sauvegarder le fournisseur
        Fournisseur fournisseur = fournisseurMapper.toEntity(request);
        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);
        FournisseurResponse response = fournisseurMapper.toResponse(savedFournisseur);

        // Publier l'événement
        fournisseurEventProducer.publishFournisseurCreated(response);

        log.info("Fournisseur créé avec succès: {}", savedFournisseur.getIdFournisseur());
        return response;
    }

    @Transactional
    public FournisseurResponse updateFournisseur(UUID fournisseurId, FournisseurUpdateRequest request) {
        log.info("Mise à jour du fournisseur: {}", fournisseurId);

        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId));

        // Mise à jour
        fournisseurMapper.updateEntityFromRequest(request, fournisseur);
        Fournisseur updatedFournisseur = fournisseurRepository.save(fournisseur);
        FournisseurResponse response = fournisseurMapper.toResponse(updatedFournisseur);

        // Publier l'événement
        fournisseurEventProducer.publishFournisseurUpdated(response);

        log.info("Fournisseur mis à jour avec succès: {}", fournisseurId);
        return response;
    }

    @Transactional(readOnly = true)
    public FournisseurResponse getFournisseurById(UUID fournisseurId) {
        log.info("Récupération du fournisseur: {}", fournisseurId);

        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId));

        return fournisseurMapper.toResponse(fournisseur);
    }

    @Transactional(readOnly = true)
    public FournisseurResponse getFournisseurByUsername(String username) {
        log.info("Récupération du fournisseur par username: {}", username);

        Fournisseur fournisseur = fournisseurRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur non trouvé avec username: " + username));

        return fournisseurMapper.toResponse(fournisseur);
    }

    @Transactional(readOnly = true)
    public List<FournisseurResponse> getAllFournisseurs() {
        log.info("Récupération de tous les fournisseurs");
        List<Fournisseur> fournisseurs = fournisseurRepository.findAll();
        return fournisseurMapper.toResponseList(fournisseurs);
    }

    @Transactional(readOnly = true)
    public List<FournisseurResponse> getActiveFournisseurs() {
        log.info("Récupération des fournisseurs actifs");
        List<Fournisseur> fournisseurs = fournisseurRepository.findByActifTrue();
        return fournisseurMapper.toResponseList(fournisseurs);
    }

    @Transactional(readOnly = true)
    public List<FournisseurResponse> getFournisseursByCategorie(String categorie) {
        log.info("Récupération des fournisseurs par catégorie: {}", categorie);
        List<Fournisseur> fournisseurs = fournisseurRepository.findByCategorie(categorie);
        return fournisseurMapper.toResponseList(fournisseurs);
    }

    @Transactional
    public void deleteFournisseur(UUID fournisseurId) {
        log.info("Suppression du fournisseur: {}", fournisseurId);

        if (!fournisseurRepository.existsById(fournisseurId)) {
            throw new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId);
        }

        fournisseurRepository.deleteById(fournisseurId);

        // Publier l'événement
        fournisseurEventProducer.publishFournisseurDeleted(fournisseurId);

        log.info("Fournisseur supprimé avec succès: {}", fournisseurId);
    }

    @Transactional
    public FournisseurResponse updateSolde(UUID fournisseurId, Double montant) {
        log.info("Mise à jour du solde du fournisseur {}: {}", fournisseurId, montant);

        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId));

        fournisseur.setSoldeCourant(fournisseur.getSoldeCourant() + montant);
        Fournisseur updatedFournisseur = fournisseurRepository.save(fournisseur);

        return fournisseurMapper.toResponse(updatedFournisseur);
    }

    @Transactional
    public FournisseurResponse desactiverFournisseur(UUID fournisseurId) {
        log.info("Désactivation du fournisseur: {}", fournisseurId);

        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId));

        fournisseur.setActif(false);
        Fournisseur updatedFournisseur = fournisseurRepository.save(fournisseur);

        return fournisseurMapper.toResponse(updatedFournisseur);
    }

    @Transactional
    public FournisseurResponse activerFournisseur(UUID fournisseurId) {
        log.info("Activation du fournisseur: {}", fournisseurId);

        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId));

        fournisseur.setActif(true);
        Fournisseur updatedFournisseur = fournisseurRepository.save(fournisseur);

        return fournisseurMapper.toResponse(updatedFournisseur);
    }

    @Transactional(readOnly = true)
    public Long countActiveFournisseurs() {
        return fournisseurRepository.countByActifTrue();
    }
}
