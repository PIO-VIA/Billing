package com.example.account.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration du cache Caffeine pour améliorer les performances
 *
 * Stratégie de cache :
 * - clients : Données référentielles, rarement modifiées (TTL: 30 min)
 * - produits : Catalogue produit, modifications peu fréquentes (TTL: 15 min)
 * - taxes : Configuration des taxes, très stable (TTL: 1 heure)
 * - factures : Factures récemment consultées (TTL: 5 min, taille limitée)
 * - devis : Devis actifs (TTL: 10 min)
 * - fournisseurs : Données fournisseurs (TTL: 30 min)
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    /**
     * Configuration du cache manager Caffeine
     *
     * Caffeine est choisi pour :
     * - Haute performance (plus rapide que Guava)
     * - Faible empreinte mémoire
     * - Pas de dépendance réseau (contrairement à Redis)
     * - Intégré directement dans l'application
     */
    @Bean
    public CacheManager cacheManager() {
        log.info("Configuration du cache Caffeine");

        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "clients",
            "produits",
            "taxes",
            "factures",
            "devis",
            "fournisseurs",
            "banques",
            "stocks",
            "paiements"
        );

        cacheManager.setCaffeine(defaultCaffeineConfig());

        log.info("Cache Caffeine configuré avec succès : {} caches",
            cacheManager.getCacheNames().size());

        return cacheManager;
    }

    /**
     * Configuration par défaut pour tous les caches
     */
    private Caffeine<Object, Object> defaultCaffeineConfig() {
        return Caffeine.newBuilder()
            .initialCapacity(100)              // Capacité initiale
            .maximumSize(500)                  // Taille maximale (évite OOM)
            .expireAfterWrite(10, TimeUnit.MINUTES)  // TTL par défaut
            .recordStats()                     // Active les statistiques pour monitoring
            .evictionListener((key, value, cause) ->
                log.debug("Éviction du cache - key: {}, cause: {}", key, cause)
            );
    }

    /**
     * Configuration spécifique pour le cache des clients
     * TTL plus long car données rarement modifiées
     */
    @Bean
    public Caffeine<Object, Object> clientsCaffeineConfig() {
        return Caffeine.newBuilder()
            .initialCapacity(200)
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .recordStats();
    }

    /**
     * Configuration spécifique pour le cache des factures
     * TTL plus court car données fréquemment modifiées
     */
    @Bean
    public Caffeine<Object, Object> facturesCaffeineConfig() {
        return Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(300)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterAccess(2, TimeUnit.MINUTES)  // Expire si non accédé pendant 2 min
            .recordStats();
    }

    /**
     * Configuration spécifique pour le cache des taxes
     * TTL très long car données très stables
     */
    @Bean
    public Caffeine<Object, Object> taxesCaffeineConfig() {
        return Caffeine.newBuilder()
            .initialCapacity(50)
            .maximumSize(100)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .recordStats();
    }
}
