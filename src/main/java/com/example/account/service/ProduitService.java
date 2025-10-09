package com.example.account.service;

import com.example.account.model.entity.Produit;
import com.example.account.repository.ProduitRepository;
import com.example.account.service.producer.ProduitEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final ProduitEventProducer produitEventProducer;

    @Transactional
    public Produit createProduit(Produit produit) {
        log.info("Création d'un nouveau produit: {}", produit.getNomProduit());

        // Vérifications
        if (produit.getReference() != null && produitRepository.existsByReference(produit.getReference())) {
            throw new IllegalArgumentException("Un produit avec cette référence existe déjà");
        }

        // Définir les timestamps
        produit.setIdProduit(UUID.randomUUID());
        produit.setCreatedAt(LocalDateTime.now());
        produit.setUpdatedAt(LocalDateTime.now());

        // Créer et sauvegarder le produit
        Produit savedProduit = produitRepository.save(produit);

        // Publier l'événement
        produitEventProducer.publishProduitCreated(savedProduit);

        log.info("Produit créé avec succès: {}", savedProduit.getIdProduit());
        return savedProduit;
    }

    @Transactional
    public Produit updateProduit(UUID produitId, Produit produitDetails) {
        log.info("Mise à jour du produit: {}", produitId);

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé: " + produitId));

        // Mise à jour des champs
        if (produitDetails.getNomProduit() != null) produit.setNomProduit(produitDetails.getNomProduit());
        if (produitDetails.getTypeProduit() != null) produit.setTypeProduit(produitDetails.getTypeProduit());
        if (produitDetails.getPrixVente() != null) produit.setPrixVente(produitDetails.getPrixVente());
        if (produitDetails.getCout() != null) produit.setCout(produitDetails.getCout());
        if (produitDetails.getCategorie() != null) produit.setCategorie(produitDetails.getCategorie());
        if (produitDetails.getReference() != null) produit.setReference(produitDetails.getReference());
        if (produitDetails.getCodeBarre() != null) produit.setCodeBarre(produitDetails.getCodeBarre());
        if (produitDetails.getPhoto() != null) produit.setPhoto(produitDetails.getPhoto());
        if (produitDetails.getActive() != null) produit.setActive(produitDetails.getActive());

        produit.setUpdatedAt(LocalDateTime.now());

        Produit updatedProduit = produitRepository.save(produit);

        // Publier l'événement
        produitEventProducer.publishProduitUpdated(updatedProduit);

        log.info("Produit mis à jour avec succès: {}", produitId);
        return updatedProduit;
    }

    @Transactional(readOnly = true)
    public Produit getProduitById(UUID produitId) {
        log.info("Récupération du produit: {}", produitId);

        return produitRepository.findById(produitId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé: " + produitId));
    }

    @Transactional(readOnly = true)
    public Produit getProduitByReference(String reference) {
        log.info("Récupération du produit par référence: {}", reference);

        return produitRepository.findByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec référence: " + reference));
    }

    @Transactional(readOnly = true)
    public Produit getProduitByCodeBarre(String codeBarre) {
        log.info("Récupération du produit par code-barre: {}", codeBarre);

        return produitRepository.findByCodeBarre(codeBarre)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec code-barre: " + codeBarre));
    }

    @Transactional(readOnly = true)
    public List<Produit> getAllProduits() {
        log.info("Récupération de tous les produits");
        return produitRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Produit> getAllProduits(Pageable pageable) {
        log.info("Récupération de tous les produits avec pagination");
        return produitRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Produit> getActiveProduits() {
        log.info("Récupération des produits actifs");
        return produitRepository.findAllActiveProducts();
    }

    @Transactional(readOnly = true)
    public List<Produit> getProduitsByCategorie(String categorie) {
        log.info("Récupération des produits par catégorie: {}", categorie);
        return produitRepository.findByCategorie(categorie);
    }

    @Transactional(readOnly = true)
    public List<Produit> getProduitsByType(String typeProduit) {
        log.info("Récupération des produits par type: {}", typeProduit);
        return produitRepository.findByTypeProduit(typeProduit);
    }

    @Transactional(readOnly = true)
    public List<Produit> searchProduitsByNom(String nomProduit) {
        log.info("Recherche des produits par nom: {}", nomProduit);
        return produitRepository.findByNomProduitContaining(nomProduit);
    }

    @Transactional(readOnly = true)
    public List<Produit> getProduitsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Récupération des produits entre {} et {}", minPrice, maxPrice);
        return produitRepository.findByPrixVenteBetween(minPrice, maxPrice);
    }

    @Transactional
    public void deleteProduit(UUID produitId) {
        log.info("Suppression du produit: {}", produitId);

        if (!produitRepository.existsById(produitId)) {
            throw new IllegalArgumentException("Produit non trouvé: " + produitId);
        }

        produitRepository.deleteById(produitId);

        // Publier l'événement
        produitEventProducer.publishProduitDeleted(produitId);

        log.info("Produit supprimé avec succès: {}", produitId);
    }

    @Transactional
    public Produit desactiverProduit(UUID produitId) {
        log.info("Désactivation du produit: {}", produitId);

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé: " + produitId));

        produit.setActive(false);
        produit.setUpdatedAt(LocalDateTime.now());

        return produitRepository.save(produit);
    }

    @Transactional
    public Produit activerProduit(UUID produitId) {
        log.info("Activation du produit: {}", produitId);

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé: " + produitId));

        produit.setActive(true);
        produit.setUpdatedAt(LocalDateTime.now());

        return produitRepository.save(produit);
    }

    @Transactional(readOnly = true)
    public Long countByCategorie(String categorie) {
        return produitRepository.countByCategorie(categorie);
    }

    @Transactional(readOnly = true)
    public Long countByType(String typeProduit) {
        return produitRepository.countByTypeProduit(typeProduit);
    }
}
