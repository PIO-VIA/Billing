# Configuration Kafka - Topics et Événements

Ce document décrit la configuration Kafka mise en place pour le module de comptabilité.

## Topics Configurés

### Topics Client
- **client-created**: Événements de création de clients
- **client-updated**: Événements de mise à jour de clients
- **client-deleted**: Événements de suppression de clients

### Topics Facture
- **facture-created**: Événements de création de factures
- **facture-updated**: Événements de mise à jour de factures
- **facture-deleted**: Événements de suppression de factures
- **facture-paid**: Événements de paiement complet de factures

### Topics Devis
- **devis-created**: Événements de création de devis
- **devis-updated**: Événements de mise à jour de devis
- **devis-deleted**: Événements de suppression de devis
- **devis-accepted**: Événements d'acceptation de devis

### Topics Paiement
- **paiement-created**: Événements de création de paiements
- **paiement-updated**: Événements de mise à jour de paiements
- **paiement-deleted**: Événements de suppression de paiements

### Topics Produit
- **produit-created**: Événements de création de produits
- **produit-updated**: Événements de mise à jour de produits
- **produit-deleted**: Événements de suppression de produits

### Topics Fournisseur
- **fournisseur-created**: Événements de création de fournisseurs
- **fournisseur-updated**: Événements de mise à jour de fournisseurs
- **fournisseur-deleted**: Événements de suppression de fournisseurs

### Topics Système
- **notification**: Événements de notification générale
- **audit**: Événements d'audit et de traçabilité

## Configuration

Tous les topics sont configurés avec:
- **Partitions**: 3
- **Réplicas**: 1
- **Auto-création**: Activée

## Producers

### KafkaProducerService
Service générique pour l'envoi de messages Kafka avec gestion des callbacks et logging.

### Event Producers
- **ClientEventProducer**: Gestion des événements clients
- **FactureEventProducer**: Gestion des événements factures
- **DevisEventProducer**: Gestion des événements devis
- **PaiementEventProducer**: Gestion des événements paiements
- **ProduitEventProducer**: Gestion des événements produits
- **FournisseurEventProducer**: Gestion des événements fournisseurs

## Consumers

### ClientEventConsumer
Traite les événements:
- Création de clients
- Mise à jour de clients
- Suppression de clients

### FactureEventConsumer
Traite les événements:
- Création de factures
- Mise à jour de factures
- Paiement de factures
- Suppression de factures

### PaiementEventConsumer
Traite les événements:
- Création de paiements
- Mise à jour de paiements
- Suppression de paiements

### AuditEventConsumer
Traite les événements:
- Audit des opérations
- Notifications système

## Configuration Application

### Producer
```properties
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
```

### Consumer
```properties
spring.kafka.consumer.group-id=my-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
```

## Services Implémentés

### ClientService
- CRUD complet des clients
- Gestion du solde client
- Publication d'événements Kafka

### FactureService
- CRUD complet des factures
- Gestion des paiements
- Calcul automatique des montants
- Publication d'événements Kafka

### DevisService
- CRUD complet des devis
- Acceptation/Refus de devis
- Gestion des dates de validité
- Publication d'événements Kafka

### PaiementService
- CRUD complet des paiements
- Lien avec les factures
- Calculs de totaux par période/client
- Publication d'événements Kafka

### ProduitService
- CRUD complet des produits
- Gestion de l'activation/désactivation
- Recherche par catégorie, type, prix
- Publication d'événements Kafka

### FournisseurService
- CRUD complet des fournisseurs
- Gestion du solde fournisseur
- Activation/Désactivation
- Publication d'événements Kafka

## Utilisation

### Exemple: Créer un client
```java
@Autowired
private ClientService clientService;

ClientCreateRequest request = ClientCreateRequest.builder()
    .username("client1")
    .email("client@example.com")
    .adresse("123 Rue Example")
    .typeClient(TypeClient.ENTREPRISE)
    .build();

ClientResponse response = clientService.createClient(request);
// Un événement client-created est automatiquement publié sur Kafka
```

### Exemple: Traiter un événement
```java
// Le consumer ClientEventConsumer traite automatiquement l'événement
@KafkaListener(topics = "client-created", groupId = "${spring.kafka.consumer.group-id}")
public void consumeClientCreated(@Payload String message, Acknowledgment acknowledgment) {
    // Traitement de l'événement
    // Ex: envoi email, mise à jour cache, etc.
    acknowledgment.acknowledge();
}
```

## Notes Importantes

1. **Idempotence**: Les producers sont configurés avec l'idempotence activée
2. **Acknowledgment**: Les consumers utilisent un acknowledgment manuel
3. **Serialization**: JSON est utilisé pour la sérialisation des messages
4. **Error Handling**: Les erreurs sont loggées et les messages non acknowledgés sont retraités
5. **Concurrency**: Les consumers sont configurés avec une concurrency de 3

## Prochaines Étapes

Pour démarrer Kafka localement, utilisez:
```bash
docker-compose up -d
```

Le fichier `docker-compose.yml` doit contenir la configuration Kafka et Zookeeper.
