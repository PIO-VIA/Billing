package com.example.account.service;

import com.example.account.dto.request.ProduitCreateRequest;
import com.example.account.dto.request.ProduitUpdateRequest;
import com.example.account.dto.response.ProduitResponse;
import com.example.account.mapper.ProduitMapper;
import com.example.account.model.entity.Produit;
import com.example.account.repository.ProduitRepository;
import com.example.account.service.producer.ProduitEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    private final ProduitMapper produitMapper;

    @Transactional
    public ProduitResponse createProduit(ProduitCreateRequest request) {
        log.info("Création d'un nouveau produit: {}", request.getNomProduit());

        // Vérifications
        if (request.getReference() != null && produitRepository.existsByReference(request.getReference())) {
            throw new IllegalArgumentException("Un produit avec cette référence existe déjà");
        }

        // Créer et sauvegarder le produit
        Produit produit = produitMapper.toEntity(request);
        Produit savedProduit = produitRepository.save(produit);
        ProduitResponse response = produitMapper.toResponse(savedProduit);

        // Publier l'événement
        produitEventProducer.publishProduitCreated(response);

        log.info("Produit créé avec succès: {}", savedProduit.getIdProduit());
        return response;
    }

    @Transactional
    @CachePut(value = "produits", key = "#produitId")
    public ProduitResponse updateProduit(UUID produitId, ProduitUpdateRequest request) {
        log.info("Mise à jour du produit: {}", produitId);

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé: " + produitId));

        // Mise à jour
        produitMapper.updateEntityFromRequest(request, produit);
        Produit updatedProduit = produitRepository.save(produit);
        ProduitResponse response = produitMapper.toResponse(updatedProduit);

        // Publier l'événement
        produitEventProducer.publishProduitUpdated(response);

        log.info("Produit mis à jour avec succès: {}", produitId);
        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "produits", key = "#produitId")
    public ProduitResponse getProduitById(UUID produitId) {
        log.info("Récupération du produit: {}", produitId);

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé: " + produitId));

        return produitMapper.toResponse(produit);
    }

    @Transactional(readOnly = true)
    public ProduitResponse getProduitByReference(String reference) {
        log.info("Récupération du produit par référence: {}", reference);

        Produit produit = produitRepository.findByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec référence: " + reference));

        return produitMapper.toResponse(produit);
    }

    @Transactional(readOnly = true)
    public ProduitResponse getProduitByCodeBarre(String codeBarre) {
        log.info("Récupération du produit par code-barre: {}", codeBarre);

        Produit produit = produitRepository.findByCodeBarre(codeBarre)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec code-barre: " + codeBarre));

        return produitMapper.toResponse(produit);
    }

    @Transactional(readOnly = true)
    public List<ProduitResponse> getAllProduits() {
        log.info("Récupération de tous les produits");
        List<Produit> produits = produitRepository.findAll();
        return produitMapper.toResponseList(produits);
    }

    @Transactional(readOnly = true)
    public Page<ProduitResponse> getAllProduits(Pageable pageable) {
        log.info("Récupération de tous les produits avec pagination");
        return produitRepository.findAll(pageable).map(produitMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ProduitResponse> getActiveProduits() {
        log.info("Récupération des produits actifs");
        List<Produit> produits = produitRepository.findAllActiveProducts();
        return produitMapper.toResponseList(produits);
    }

    @Transactional(readOnly = true)
    public List<ProduitResponse> getProduitsByCategorie(String categorie) {
        log.info("Récupération des produits par catégorie: {}", categorie);
        List<Produit> produits = produitRepository.findByCategorie(categorie);
        return produitMapper.toResponseList(produits);
    }

    @Transactional(readOnly = true)
    public List<ProduitResponse> getProduitsByType(String typeProduit) {
        log.info("Récupération des produits par type: {}", typeProduit);
        List<Produit> produits = produitRepository.findByTypeProduit(typeProduit);
        return produitMapper.toResponseList(produits);
    }

    @Transactional(readOnly = true)
    public List<ProduitResponse> searchProduitsByNom(String nomProduit) {
        log.info("Recherche des produits par nom: {}", nomProduit);
        List<Produit> produits = produitRepository.findByNomProduitContaining(nomProduit);
        return produitMapper.toResponseList(produits);
    }

    @Transactional(readOnly = true)
    public List<ProduitResponse> getProduitsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Récupération des produits entre {} et {}", minPrice, maxPrice);
        List<Produit> produits = produitRepository.findByPrixVenteBetween(minPrice, maxPrice);
        return produitMapper.toResponseList(produits);
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
    public ProduitResponse desactiverProduit(UUID produitId) {
        log.info("Désactivation du produit: {}", produitId);

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé: " + produitId));

        produit.setActive(false);
        produit.setUpdatedAt(LocalDateTime.now());

        Produit savedProduit = produitRepository.save(produit);
        return produitMapper.toResponse(savedProduit);
    }

    @Transactional
    public ProduitResponse activerProduit(UUID produitId) {
        log.info("Activation du produit: {}", produitId);

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé: " + produitId));

        produit.setActive(true);
        produit.setUpdatedAt(LocalDateTime.now());

        Produit savedProduit = produitRepository.save(produit);
        return produitMapper.toResponse(savedProduit);
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
