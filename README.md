# Service de Facturation (Billing Service)

Ce projet est un microservice de facturation développé avec Spring Boot. Il fournit une API RESTful pour gérer les entités comptables de base telles que les clients, les fournisseurs, les devis, les factures et les paiements.

## Table des matières
- [Fonctionnalités](#fonctionnalités)
- [Technologies Utilisées](#technologies-utilisées)
- [Prérequis](#prérequis)
- [Installation et Lancement](#installation-et-lancement)
- [Documentation de l'API](#documentation-de-lapi)
- [Lancer les tests](#lancer-les-tests)
- [Structure du Projet](#structure-du-projet)

## Fonctionnalités
- **Gestion des Clients** : Opérations CRUD complètes pour les clients.
- **Gestion des Fournisseurs** : Opérations CRUD complètes pour les fournisseurs.
- **Gestion des Devis** : Création, mise à jour et suivi des devis.
- **Gestion des Factures** : Création, mise à jour, suivi des factures et génération d'avoirs.
- **Gestion des Paiements** : Enregistrement et suivi des paiements reçus.
- **Communication Événementielle** : Utilisation d'Apache Kafka pour la production et la consommation d'événements liés aux entités (clients, factures, etc.).
- **Génération de PDF** : Création de documents PDF pour les factures et les reçus de paiement.
- **Notifications par E-mail** : Envoi d'e-mails pour des événements clés (création de facture, confirmation de paiement).
- **Documentation API** : Documentation automatique des endpoints avec Swagger/OpenAPI.

## Technologies Utilisées
- **Langage** : Java 21
- **Framework** : Spring Boot 3.5.6
- **Accès aux données** : Spring Data JPA, Hibernate
- **Base de données** : PostgreSQL
- **Messagerie** : Spring Kafka
- **Gestion de projet** : Maven
- **Mapping d'objets** : MapStruct
- **Utilitaires** : Lombok
- **Conteneurisation** : Docker, Docker Compose
- **Documentation API** : SpringDoc (Swagger UI)
- **Moteur de template** : Thymeleaf (pour les e-mails et les templates PDF)

## Prérequis
Avant de commencer, assurez-vous d'avoir les outils suivants installés sur votre machine :
- JDK 21 ou supérieur
- Maven 3.8 ou supérieur
- Docker et Docker Compose

## Installation et Lancement

1.  **Clonez le dépôt**
    ```bash
    git clone https://github.com/PIO-VIA/Billing.git
    cd Billing
    ```

2.  **Lancez les services externes**
    Le projet utilise Docker Compose pour démarrer les services dépendants (PostgreSQL, Kafka, Zookeeper).
    ```bash
    docker compose up -d
    ```

3.  **Configurez l'application**
    Les configurations pour la base de données, Kafka et le service de messagerie se trouvent dans le fichier `src/main/resources/application.properties`. Modifiez-le si nécessaire pour votre environnement local.

4.  **Lancez l'application**
    Utilisez le wrapper Maven pour lancer l'application Spring Boot.
    ```bash
    ./mvnw spring-boot:run
    ```
    L'application démarrera et sera accessible par défaut à l'adresse `http://localhost:8080`.

## Documentation de l'API
Une fois l'application lancée, la documentation interactive de l'API est disponible via Swagger UI. Vous pouvez y accéder à l'adresse suivante :

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Lancer les tests
Pour exécuter la suite de tests unitaires et d'intégration, utilisez la commande Maven suivante :
```bash
./mvnw test
```

## Structure du Projet
```
/src
├───/main
│   ├───/java/com/example/account
│   │   ├───/config         # Configurations Spring (Kafka, Swagger, etc.)
│   │   ├───/controller     # Endpoints de l'API REST
│   │   ├───/dto            # Data Transfer Objects (pour les requêtes et réponses)
│   │   ├───/mapper         # Mappers MapStruct (conversion DTO <-> Entité)
│   │   ├───/model          # Entités JPA et Enums
│   │   ├───/repository     # Interfaces Spring Data JPA
│   │   └───/service        # Logique métier et services
│   └───/resources
│       ├───/templates      # Templates HTML (e-mails, PDF)
│       └───application.properties # Fichier de configuration principal
└───/test                   # Tests unitaires et d'intégration
```
