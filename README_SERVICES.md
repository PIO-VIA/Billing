# Module de Comptabilité - Services et Kafka

## Vue d'ensemble

Ce module implémente un système de comptabilité complet avec les fonctionnalités suivantes :
- Gestion des clients
- Gestion des fournisseurs
- Gestion des factures
- Gestion des devis
- Gestion des paiements
- Gestion des produits
- Système d'événements asynchrone avec Kafka

## Architecture

### Couches de l'application

```
┌─────────────────────────────────────────────┐
│          Controllers (REST API)              │
├─────────────────────────────────────────────┤
│              Services                        │
│  ├─ ClientService                           │
│  ├─ FactureService                          │
│  ├─ DevisService                            │
│  ├─ PaiementService                         │
│  ├─ ProduitService                          │
│  └─ FournisseurService                      │
├─────────────────────────────────────────────┤
│         Kafka Producers                      │
│  ├─ ClientEventProducer                     │
│  ├─ FactureEventProducer                    │
│  ├─ DevisEventProducer                      │
│  ├─ PaiementEventProducer                   │
│  ├─ ProduitEventProducer                    │
│  └─ FournisseurEventProducer                │
├─────────────────────────────────────────────┤
│         Kafka Consumers                      │
│  ├─ ClientEventConsumer                     │
│  ├─ FactureEventConsumer                    │
│  ├─ PaiementEventConsumer                   │
│  └─ AuditEventConsumer                      │
├─────────────────────────────────────────────┤
│         Repositories (JPA)                   │
├─────────────────────────────────────────────┤
│         Database (PostgreSQL)                │
└─────────────────────────────────────────────┘
```

## Services Implémentés

### 1. ClientService
**Fonctionnalités:**
- Création de clients avec validation
- Mise à jour des informations client
- Gestion du solde client
- Filtrage par type de client (Particulier/Entreprise)
- Récupération des clients actifs
- Suppression de clients

**Événements Kafka publiés:**
- `client-created`
- `client-updated`
- `client-deleted`

### 2. FactureService
**Fonctionnalités:**
- Création de factures avec calcul automatique des montants
- Mise à jour de factures
- Gestion des états (Brouillon, Envoyé, Payé, etc.)
- Enregistrement de paiements partiels
- Marquage de factures comme payées
- Recherche de factures en retard
- Recherche de factures non payées
- Filtrage par période, client, état

**Événements Kafka publiés:**
- `facture-created`
- `facture-updated`
- `facture-deleted`
- `facture-paid`

### 3. DevisService
**Fonctionnalités:**
- Création de devis avec génération automatique du numéro
- Mise à jour de devis
- Acceptation/Refus de devis
- Gestion de la validité des devis
- Recherche de devis expirés
- Calcul automatique avec remises

**Événements Kafka publiés:**
- `devis-created`
- `devis-updated`
- `devis-deleted`
- `devis-accepted`

### 4. PaiementService
**Fonctionnalités:**
- Enregistrement de paiements
- Liaison automatique avec les factures
- Calcul des totaux par client/facture/période
- Filtrage par mode de paiement
- Statistiques de paiement

**Événements Kafka publiés:**
- `paiement-created`
- `paiement-updated`
- `paiement-deleted`

### 5. ProduitService
**Fonctionnalités:**
- CRUD complet des produits
- Recherche par nom, catégorie, type
- Filtrage par plage de prix
- Activation/Désactivation de produits
- Statistiques par catégorie/type

**Événements Kafka publiés:**
- `produit-created`
- `produit-updated`
- `produit-deleted`

### 6. FournisseurService
**Fonctionnalités:**
- Gestion complète des fournisseurs
- Gestion du solde fournisseur
- Activation/Désactivation
- Filtrage par catégorie
- Comptage des fournisseurs actifs

**Événements Kafka publiés:**
- `fournisseur-created`
- `fournisseur-updated`
- `fournisseur-deleted`

## API REST - Endpoints Principaux

### Clients
```
POST   /api/clients                    - Créer un client
GET    /api/clients                    - Liste tous les clients
GET    /api/clients/{id}               - Récupérer un client
GET    /api/clients/actifs             - Clients actifs
PUT    /api/clients/{id}               - Mettre à jour
DELETE /api/clients/{id}               - Supprimer
PUT    /api/clients/{id}/solde         - Mettre à jour le solde
```

### Factures
```
POST   /api/factures                   - Créer une facture
GET    /api/factures                   - Liste toutes les factures
GET    /api/factures/{id}              - Récupérer une facture
GET    /api/factures/retard            - Factures en retard
GET    /api/factures/non-payees        - Factures non payées
PUT    /api/factures/{id}/marquer-paye - Marquer comme payée
PUT    /api/factures/{id}/paiement     - Enregistrer un paiement
DELETE /api/factures/{id}              - Supprimer
```

### Devis
```
POST   /api/devis                      - Créer un devis
GET    /api/devis                      - Liste tous les devis
GET    /api/devis/{id}                 - Récupérer un devis
GET    /api/devis/expires              - Devis expirés
PUT    /api/devis/{id}/accepter        - Accepter un devis
PUT    /api/devis/{id}/refuser         - Refuser un devis
DELETE /api/devis/{id}                 - Supprimer
```

### Paiements
```
POST   /api/paiements                  - Créer un paiement
GET    /api/paiements                  - Liste tous les paiements
GET    /api/paiements/client/{id}      - Paiements d'un client
GET    /api/paiements/facture/{id}     - Paiements d'une facture
GET    /api/paiements/total/client/{id}- Total paiements client
DELETE /api/paiements/{id}             - Supprimer
```

### Fournisseurs
```
POST   /api/fournisseurs               - Créer un fournisseur
GET    /api/fournisseurs               - Liste tous les fournisseurs
GET    /api/fournisseurs/actifs        - Fournisseurs actifs
PUT    /api/fournisseurs/{id}/activer  - Activer
PUT    /api/fournisseurs/{id}/desactiver - Désactiver
DELETE /api/fournisseurs/{id}          - Supprimer
```

## Configuration Kafka

### Topics créés automatiquement
Tous les topics sont créés avec 3 partitions et 1 réplica :
- Topics Client : client-created, client-updated, client-deleted
- Topics Facture : facture-created, facture-updated, facture-deleted, facture-paid
- Topics Devis : devis-created, devis-updated, devis-deleted, devis-accepted
- Topics Paiement : paiement-created, paiement-updated, paiement-deleted
- Topics Produit : produit-created, produit-updated, produit-deleted
- Topics Fournisseur : fournisseur-created, fournisseur-updated, fournisseur-deleted
- Topics Système : notification, audit

### Configuration Producer
```properties
spring.kafka.producer.key-serializer=StringSerializer
spring.kafka.producer.value-serializer=JsonSerializer
spring.kafka.producer.acks=all
spring.kafka.producer.retries=3
spring.kafka.producer.enable-idempotence=true
```

### Configuration Consumer
```properties
spring.kafka.consumer.group-id=my-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.enable-auto-commit=false
```

## Démarrage

### 1. Démarrer Kafka et PostgreSQL
```bash
docker-compose up -d
```

### 2. Compiler le projet
```bash
./mvnw clean install
```

### 3. Lancer l'application
```bash
./mvnw spring-boot:run
```

### 4. Accéder à la documentation Swagger
```
http://localhost:8080/docs
```

## Tests avec cURL

### Créer un client
```bash
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "username": "client_test",
    "email": "test@example.com",
    "adresse": "123 Rue Test",
    "typeClient": "ENTREPRISE",
    "categorie": "IT"
  }'
```

### Créer une facture
```bash
curl -X POST http://localhost:8080/api/factures \
  -H "Content-Type: application/json" \
  -d '{
    "dateFacturation": "2025-10-09",
    "dateEcheance": "2025-11-09",
    "idClient": "uuid-du-client",
    "etat": "BROUILLON",
    "devise": "EUR"
  }'
```

### Créer un paiement
```bash
curl -X POST http://localhost:8080/api/paiements \
  -H "Content-Type: application/json" \
  -d '{
    "idClient": "uuid-du-client",
    "idFacture": "uuid-de-la-facture",
    "montant": 1000.00,
    "date": "2025-10-09",
    "journal": "BANQUE",
    "modePaiement": "VIREMENT"
  }'
```

## Monitoring Kafka

### Vérifier les topics
```bash
docker exec -it account-kafka-1 kafka-topics --list --bootstrap-server localhost:9092
```

### Consommer des messages
```bash
docker exec -it account-kafka-1 kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic client-created \
  --from-beginning
```

## Logs

Les services loggent les événements importants :
- Création/Modification/Suppression d'entités
- Publication d'événements Kafka
- Consommation d'événements Kafka
- Erreurs et exceptions

Exemple de log :
```
INFO  c.e.a.service.ClientService - Création d'un nouveau client: client_test
INFO  c.e.a.service.ClientService - Client créé avec succès: 123e4567-e89b-12d3-a456-426614174000
INFO  c.e.a.s.p.ClientEventProducer - Publication de l'événement client créé: 123e4567-e89b-12d3-a456-426614174000
```

## Gestion des Erreurs

Tous les services gèrent les erreurs courantes :
- Entité non trouvée : `IllegalArgumentException`
- Doublons : `IllegalArgumentException`
- Validation : Annotations Jakarta Validation
- Erreurs Kafka : Loggées et message non acknowledge pour retraitement

## Prochaines Étapes

1. **Sécurité** : Ajouter Spring Security et JWT
2. **Tests** : Ajouter des tests unitaires et d'intégration
3. **Cache** : Implémenter Redis pour le cache
4. **Rapports** : Génération de rapports PDF
5. **Emails** : Notifications par email pour les événements importants
6. **Workflows** : Système d'approbation pour les factures
7. **Analytics** : Tableaux de bord et KPIs

## Contribution

Pour contribuer au projet :
1. Créer une branche feature
2. Implémenter les modifications
3. Ajouter des tests
4. Créer une pull request

## Support

Pour toute question ou problème, consulter :
- Documentation Kafka : KAFKA_TOPICS.md
- Logs de l'application
- Documentation Swagger : http://localhost:8080/docs
