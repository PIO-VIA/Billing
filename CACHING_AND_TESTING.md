# 🚀 Caching et Tests d'Intégration - Guide Complet

## 📋 Table des Matières

1. [Caching avec Caffeine](#caching-avec-caffeine)
2. [Tests d'Intégration avec TestContainers](#tests-dintégration-avec-testcontainers)
3. [Métriques et Monitoring](#métriques-et-monitoring)
4. [Commandes Utiles](#commandes-utiles)

---

## 🎯 Caching avec Caffeine

### Configuration

Le caching est configuré dans `CacheConfig.java` avec les paramètres suivants :

```java
@Configuration
@EnableCaching
public class CacheConfig {
    // 9 caches configurés : clients, produits, taxes, factures, devis,
    // fournisseurs, banques, stocks, paiements
}
```

### Stratégie de Cache

| Cache | TTL | Taille Max | Usage |
|-------|-----|------------|-------|
| **clients** | 30 min | 1000 | Données référentielles clients |
| **produits** | 15 min | 500 | Catalogue produits |
| **taxes** | 1 heure | 100 | Configuration taxes (très stable) |
| **factures** | 5 min | 300 | Factures récentes |
| **devis** | 10 min | 500 | Devis actifs |
| **fournisseurs** | 30 min | 500 | Données fournisseurs |
| **banques** | 10 min | 500 | Comptes bancaires |
| **stocks** | 10 min | 500 | Inventaire |
| **paiements** | 10 min | 500 | Paiements |

### Annotations Utilisées

#### @Cacheable - Mise en cache des résultats

```java
@Cacheable(value = "clients", key = "#clientId")
public ClientResponse getClientById(UUID clientId) {
    // Cette méthode ne sera exécutée que si la valeur n'est pas en cache
    // Les appels suivants retourneront la valeur cachée
}
```

**Avantage** : Réduit les requêtes SQL pour les données fréquemment consultées.

#### @CachePut - Mise à jour du cache

```java
@CachePut(value = "clients", key = "#clientId")
public ClientResponse updateClient(UUID clientId, ClientUpdateRequest request) {
    // Met à jour le cache avec la nouvelle valeur
    // Le cache est toujours synchronisé avec la base de données
}
```

**Avantage** : Le cache est rafraîchi automatiquement lors des mises à jour.

#### @CacheEvict - Suppression du cache

```java
@CacheEvict(value = "clients", key = "#clientId")
public void deleteClient(UUID clientId) {
    // Supprime l'entrée du cache
    // Évite les données obsolètes
}
```

### Services avec Cache Activé

- ✅ **ClientService** : `getClientById()`, `updateClient()`, `deleteClient()`, `updateSolde()`
- ✅ **ProduitService** : `getProduitById()`, `updateProduit()`

### Métriques de Cache

Le cache expose des métriques Prometheus accessibles via :

```
GET http://localhost:8080/actuator/metrics/cache.gets
GET http://localhost:8080/actuator/metrics/cache.puts
GET http://localhost:8080/actuator/metrics/cache.evictions
GET http://localhost:8080/actuator/caches
```

### Exemple de Statistiques

```json
{
  "cacheManager": "cacheManager",
  "caches": {
    "clients": {
      "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache",
      "name": "clients",
      "hitCount": 150,
      "missCount": 25,
      "loadSuccessCount": 25,
      "evictionCount": 5
    }
  }
}
```

**Hit Ratio** = 150 / (150 + 25) = **85.7%** → Excellent !

### Désactivation du Cache (Développement)

Pour désactiver le cache en développement :

```properties
# application-dev.properties
spring.cache.type=none
```

---

## 🧪 Tests d'Intégration avec TestContainers

### Architecture

Les tests d'intégration utilisent **TestContainers** pour démarrer automatiquement :

- 🐘 **PostgreSQL 16** (Alpine) - Base de données réelle
- 🦘 **Kafka** (Confluent Platform 7.5.0) - Messaging

### Configuration

`TestContainersConfiguration.java` démarre les conteneurs :

```java
@TestConfiguration
public class TestContainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withReuse(true);  // ⚡ Réutilise entre les tests
    }

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        return new KafkaContainer("confluentinc/cp-kafka:7.5.0")
            .withReuse(true);
    }
}
```

### Tests Créés

#### 1. FactureIntegrationTest (10 tests)

**Scénarios testés** :
- ✅ Création de facture avec persistance en base
- ✅ Récupération par ID
- ✅ Paiement partiel (400 EUR sur 1000 EUR)
- ✅ Paiement complet
- ✅ Paiement excessif (erreur 400)
- ✅ Filtrage par client
- ✅ Suppression
- ✅ Facture inexistante (404)
- ✅ Marquer comme payée
- ✅ Filtrer par statut

**Exemple de test** :

```java
@Test
@DisplayName("PUT /api/factures/{id}/paiement - Paiement partiel")
void enregistrerPaiement_partiel_shouldUpdateStatus() throws Exception {
    // Given - Facture de 1000 EUR
    Facture facture = factureRepository.save(Facture.builder()
        .montantTotal(new BigDecimal("1000.00"))
        .montantRestant(new BigDecimal("1000.00"))
        .etat(StatutFacture.ENVOYE)
        .build());

    // When - Paiement de 400 EUR
    mockMvc.perform(put("/api/factures/{id}/paiement", facture.getIdFacture())
            .param("montantPaye", "400.00"))
        // Then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.montantRestant").value(600.00))
        .andExpect(jsonPath("$.etat").value("PARTIELLEMENT_PAYE"));

    // Vérifier en base de données
    Facture updated = factureRepository.findById(facture.getIdFacture()).orElseThrow();
    assertThat(updated.getMontantRestant()).isEqualByComparingTo(new BigDecimal("600.00"));
    assertThat(updated.getEtat()).isEqualTo(StatutFacture.PARTIELLEMENT_PAYE);
}
```

#### 2. ClientIntegrationTest (10 tests)

**Scénarios testés** :
- ✅ Création client
- ✅ Username dupliqué (erreur)
- ✅ Récupération par ID
- ✅ Mise à jour
- ✅ Suppression
- ✅ Filtrage clients actifs
- ✅ Mise à jour solde
- ✅ Récupération par username
- ✅ Validation des données
- ✅ Comptage clients actifs

#### 3. PaiementIntegrationTest (8 tests)

**Scénarios testés** :
- ✅ Paiement par virement
- ✅ Paiement par carte bancaire
- ✅ Récupération par facture
- ✅ Récupération par client
- ✅ Récupération par ID
- ✅ Filtrage par type
- ✅ Suppression
- ✅ **Flow complet E2E** : 3 paiements partiels → Facture PAYEE

**Flow complet testé** :

```
Facture: 1000 EUR (ENVOYE)
  ↓
Paiement 1: 300 EUR → Montant restant: 700 EUR (PARTIELLEMENT_PAYE)
  ↓
Paiement 2: 400 EUR → Montant restant: 300 EUR (PARTIELLEMENT_PAYE)
  ↓
Paiement 3: 300 EUR → Montant restant: 0 EUR (PAYE) ✅
```

### Exécution des Tests

#### Prérequis

- ✅ Docker installé et démarré
- ✅ Java 21
- ✅ Maven

#### Commandes

```bash
# Tous les tests
./mvnw test

# Tests d'intégration uniquement
./mvnw test -Dtest=*IntegrationTest

# Test spécifique
./mvnw test -Dtest=FactureIntegrationTest

# Avec rapport de couverture
./mvnw clean verify
```

#### Temps d'exécution

- **Premier run** : ~30-40 secondes (téléchargement des images Docker)
- **Runs suivants** : ~10-15 secondes (conteneurs réutilisés)

### Avantages des Tests d'Intégration

| Aspect | Tests Unitaires | Tests d'Intégration |
|--------|-----------------|---------------------|
| Base de données | Mock (H2) | ✅ PostgreSQL réel |
| Kafka | Mock | ✅ Kafka réel |
| Transactions | Simulées | ✅ Réelles |
| Contraintes SQL | ❌ Non testées | ✅ Testées |
| Validations | Partielles | ✅ Complètes |
| Performance | Rapide | Moyen |
| Fiabilité | Moyenne | ✅ Élevée |

---

## 📊 Métriques et Monitoring

### Endpoints Actuator Activés

```
GET /actuator/health         # Santé de l'application
GET /actuator/metrics        # Liste des métriques
GET /actuator/prometheus     # Métriques au format Prometheus
GET /actuator/caches         # Statistiques de cache
GET /actuator/info           # Informations application
```

### Métriques Disponibles

#### Cache

```
cache.gets{cache="clients",result="hit"}
cache.gets{cache="clients",result="miss"}
cache.puts{cache="clients"}
cache.evictions{cache="clients"}
cache.size{cache="clients"}
```

#### JVM

```
jvm.memory.used
jvm.memory.max
jvm.threads.live
jvm.gc.pause
```

#### Application

```
http.server.requests{uri="/api/factures",method="GET",status="200"}
process.cpu.usage
process.uptime
```

### Exemple Prometheus Query

```promql
# Taux de hit du cache clients
rate(cache_gets_total{cache="clients",result="hit"}[5m])
/
rate(cache_gets_total{cache="clients"}[5m])
* 100
```

### Grafana Dashboard

Créer un dashboard avec :

1. **Cache Hit Ratio** (gauge)
2. **Cache Size** (graph)
3. **HTTP Requests** (graph)
4. **Response Times** (heatmap)

---

## 🛠️ Commandes Utiles

### Cache

```bash
# Voir les statistiques de cache
curl http://localhost:8080/actuator/caches | jq

# Invalider un cache spécifique (via code)
@Autowired
CacheManager cacheManager;

cacheManager.getCache("clients").clear();
```

### Tests

```bash
# Exécuter avec logs détaillés
./mvnw test -X

# Exécuter en parallèle
./mvnw -T 4 test

# Ignorer les tests
./mvnw clean install -DskipTests

# Rapport de couverture HTML
./mvnw clean verify
open target/site/jacoco/index.html
```

### Docker (TestContainers)

```bash
# Voir les conteneurs actifs
docker ps

# Logs d'un conteneur
docker logs <container_id>

# Nettoyer les conteneurs arrêtés
docker container prune

# Nettoyer les images TestContainers
docker rmi $(docker images | grep testcontainers | awk '{print $3}')
```

---

## 📈 Résultats Attendus

### Performance

**Avant cache** :
- GET /api/clients/{id} : ~50ms (requête SQL)
- GET /api/produits/{id} : ~40ms

**Après cache** :
- GET /api/clients/{id} : ~2ms (première fois: 50ms, suivantes: 2ms)
- GET /api/produits/{id} : ~1ms (première fois: 40ms, suivantes: 1ms)

**Gain** : **25x plus rapide** pour les données en cache !

### Couverture de Tests

**Avant** :
- 21 tests unitaires
- Couverture : ~30%

**Après** :
- 21 tests unitaires
- **28 tests d'intégration** (nouveaux)
- Couverture estimée : **~70%** ✅

---

## 🎓 Bonnes Pratiques

### Cache

1. ✅ **Toujours définir un TTL** pour éviter les données obsolètes
2. ✅ **Limiter la taille** pour éviter les OutOfMemoryError
3. ✅ **Utiliser @CachePut** sur les updates pour maintenir la cohérence
4. ✅ **Monitorer le hit ratio** (objectif > 80%)
5. ❌ **Ne pas cacher** les données qui changent fréquemment

### Tests d'Intégration

1. ✅ **Utiliser @Transactional** pour rollback automatique
2. ✅ **Nettoyer** les données après chaque test (@AfterEach)
3. ✅ **Tester les cas limites** (montant négatif, ID inexistant, etc.)
4. ✅ **Vérifier la persistance** en base après chaque opération
5. ✅ **Tester les flows complets** end-to-end

---

## 📚 Ressources

- [Caffeine Cache](https://github.com/ben-manes/caffeine)
- [TestContainers](https://www.testcontainers.org/)
- [Spring Caching](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache)
- [Prometheus Metrics](https://prometheus.io/docs/introduction/overview/)

---

## ✅ Checklist de Vérification

### Cache

- [x] CacheConfig créé avec Caffeine
- [x] @EnableCaching activé
- [x] Annotations @Cacheable sur getters
- [x] Annotations @CachePut sur updates
- [x] Annotations @CacheEvict sur deletes
- [x] Métriques cache activées
- [x] Configuration dans application.properties

### Tests d'Intégration

- [x] TestContainersConfiguration créé
- [x] PostgreSQL container configuré
- [x] Kafka container configuré
- [x] FactureIntegrationTest (10 tests)
- [x] ClientIntegrationTest (10 tests)
- [x] PaiementIntegrationTest (8 tests)
- [x] application-test.properties créé
- [x] Tests passent avec succès

### Monitoring

- [x] Actuator configuré
- [x] Prometheus metrics activés
- [x] Cache metrics activés
- [x] JVM metrics activés

---

**Total : 28 tests d'intégration + Cache optimisé = Backend Production-Ready ! 🚀**
