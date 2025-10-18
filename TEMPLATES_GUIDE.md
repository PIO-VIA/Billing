# Guide d'utilisation des Templates de Factures

## Vue d'ensemble

Le système de facturation intègre la génération automatique de PDFs et l'envoi d'emails avec des templates Thymeleaf personnalisés.

## Architecture

```
Templates
├── pdf/
│   ├── facture-template.html    - Template principal de facture PDF
│   └── recu-paiement.html        - Template de reçu de paiement PDF
└── email/
    ├── facture-creation.html     - Email de notification de facture
    ├── paiement-recu.html        - Email de confirmation de paiement
    └── rappel-paiement.html      - Email de rappel de paiement
```

## Services Créés

### 1. PdfGeneratorService

Service responsable de la génération des PDFs à partir des templates Thymeleaf.

**Méthodes principales:**
- `generateFacturePdf(Facture, Client)` - Génère un PDF de facture en mémoire
- `generateAndSaveFacturePdf(Facture, Client)` - Génère et sauvegarde un PDF de facture
- `generateRecuPaiementPdf(Paiement, Facture, Client)` - Génère un PDF de reçu
- `generateAndSaveRecuPaiementPdf(Paiement, Facture, Client)` - Génère et sauvegarde un reçu

**Stockage des PDFs:**
- Factures: `pdfs/factures/`
- Paiements: `pdfs/paiements/`

### 2. EmailService

Service responsable de l'envoi d'emails avec templates HTML.

**Méthodes principales:**
- `sendFactureCreationEmail(Facture, email, pdfAttachment)` - Envoie la facture par email
- `sendRappelPaiementEmail(Facture, email)` - Envoie un rappel de paiement
- `sendPaiementRecuEmail(Paiement, Facture, email, recuPdf)` - Envoie confirmation de paiement

## Endpoints API Ajoutés

### Factures

#### 1. Télécharger le PDF d'une facture
```http
GET /api/factures/{factureId}/pdf
```

**Réponse:** Fichier PDF en téléchargement direct

**Exemple cURL:**
```bash
curl -X GET "http://localhost:8080/api/factures/{factureId}/pdf" \
  -H "Accept: application/pdf" \
  --output facture.pdf
```

#### 2. Envoyer la facture par email
```http
POST /api/factures/{factureId}/envoyer-email
```

**Comportement:**
- Génère le PDF de la facture
- Envoie l'email au client avec le PDF en pièce jointe
- Marque la facture comme envoyée
- Met à jour la date d'envoi

**Exemple cURL:**
```bash
curl -X POST "http://localhost:8080/api/factures/{factureId}/envoyer-email"
```

#### 3. Envoyer un rappel de paiement
```http
POST /api/factures/{factureId}/rappel-paiement
```

**Comportement:**
- Envoie un email de rappel au client
- Affiche le montant restant à payer
- Rappelle la date d'échéance

**Exemple cURL:**
```bash
curl -X POST "http://localhost:8080/api/factures/{factureId}/rappel-paiement"
```

#### 4. Générer et sauvegarder le PDF
```http
POST /api/factures/{factureId}/generer-pdf
```

**Réponse:** Chemin du fichier PDF sauvegardé

**Exemple cURL:**
```bash
curl -X POST "http://localhost:8080/api/factures/{factureId}/generer-pdf"
```

## Utilisation des Templates

### Template de Facture PDF (`facture-template.html`)

**Variables Thymeleaf disponibles:**
- `${facture}` - Objet Facture complet
- `${client}` - Objet Client complet
- `${lignesFacture}` - Liste des lignes de facture
- `${logoPath}` - Chemin vers le logo de l'entreprise

**Exemple de modification:**

```html
<!-- Modifier les informations de l'entreprise -->
<div class="company-info">
    <h1>Votre Entreprise</h1>
    <p><strong>Adresse de votre entreprise</strong></p>
    <p>Ville, Code postal</p>
    <p>Tél: +XXX XX XX XX XX</p>
    <p>Email: contact@entreprise.com</p>
</div>
```

### Template Email de Facture (`facture-creation.html`)

**Variables Thymeleaf disponibles:**
- `${client}` - Nom du client
- `${numeroFacture}` - Numéro de facture
- `${dateFacturation}` - Date de facturation
- `${dateEcheance}` - Date d'échéance
- `${montantTotal}` - Montant total
- `${facture}` - Objet Facture complet
- `${baseUrl}` - URL de base de l'application

**Personnalisation:**

```html
<!-- Modifier le header de l'email -->
<div class="header">
    <h1>Votre Titre</h1>
    <p>Votre sous-titre</p>
</div>

<!-- Modifier le message -->
<p>Bonjour <strong th:text="${client}">Client</strong>,</p>
<p>Votre message personnalisé...</p>
```

## Configuration

### Paramètres dans `application.properties`

```properties
# Configuration email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-app

# URL de base pour les liens dans les emails
app.base-url=http://localhost:8080
```

### Variables d'environnement

Pour la production, utilisez les variables d'environnement:

```bash
export EMAIL_USERNAME=votre-email@domaine.com
export EMAIL_PASSWORD=votre-mot-de-passe
export APP_BASE_URL=https://votre-domaine.com
```

## Workflow Complet d'une Facture

### 1. Création de la facture
```bash
POST /api/factures
{
  "dateFacturation": "2025-10-09",
  "dateEcheance": "2025-11-09",
  "idClient": "uuid-du-client",
  "lignesFacture": [...]
}
```

### 2. Envoi par email au client
```bash
POST /api/factures/{factureId}/envoyer-email
```

**Ce qui se passe:**
1. Génération du PDF avec toutes les informations
2. Création de l'email HTML à partir du template
3. Envoi de l'email avec le PDF en pièce jointe
4. Mise à jour du statut `envoyeParEmail = true`

### 3. Rappel si non payé
```bash
POST /api/factures/{factureId}/rappel-paiement
```

### 4. Téléchargement manuel du PDF
```bash
GET /api/factures/{factureId}/pdf
```

## Personnalisation Avancée

### Ajouter un logo

1. Placez votre logo dans `src/main/resources/static/images/`
2. Modifiez le template:

```html
<img th:src="@{/images/votre-logo.png}" alt="Logo" class="logo" />
```

3. Dans `PdfGeneratorService`, mettez à jour:

```java
context.setVariable("logoPath", "file:///path/to/your/logo.png");
```

### Modifier les styles CSS

Les templates incluent des styles CSS inline pour garantir la compatibilité PDF. Pour modifier l'apparence:

```html
<style>
    .header {
        background-color: #votre-couleur;
    }

    .facture-title {
        color: #votre-couleur;
        font-size: 32px;
    }
</style>
```

### Ajouter des champs personnalisés

1. Dans le template HTML:

```html
<div class="detail-row">
    <div class="detail-label">Votre champ:</div>
    <div class="detail-value" th:text="${facture.votreChamp}">Valeur</div>
</div>
```

2. Dans l'entité `Facture.java`, ajoutez le champ:

```java
@Column(name = "votre_champ")
private String votreChamp;
```

## Dépannage

### Le PDF ne se génère pas

**Vérifications:**
1. OpenHtmlToPdf est bien dans les dépendances
2. Les templates sont dans `src/main/resources/templates/pdf/`
3. Les données de la facture sont complètes

**Logs à consulter:**
```
INFO  c.e.a.s.PdfGeneratorService - Génération du PDF pour la facture: FAC-001
ERROR c.e.a.s.PdfGeneratorService - Erreur lors de la génération du PDF
```

### Les emails ne s'envoient pas

**Vérifications:**
1. Configuration SMTP correcte dans `application.properties`
2. Mot de passe d'application Gmail (pas le mot de passe principal)
3. Adresse email du client renseignée

**Activer les logs de debug:**
```properties
logging.level.org.springframework.mail=DEBUG
```

### Le template ne s'affiche pas correctement

**Vérifications:**
1. Syntaxe Thymeleaf correcte (`th:text`, `th:if`, etc.)
2. Variables passées dans le contexte
3. Chemins des templates corrects

## Tests

### Tester la génération PDF

```java
@Test
public void testGenererPdfFacture() {
    UUID factureId = // ID d'une facture de test
    byte[] pdf = factureService.genererPdfFacture(factureId);
    assertNotNull(pdf);
    assertTrue(pdf.length > 0);
}
```

### Tester l'envoi d'email

```java
@Test
public void testEnvoyerFactureParEmail() {
    UUID factureId = // ID d'une facture de test
    factureService.envoyerFactureParEmail(factureId);

    Facture facture = factureRepository.findById(factureId).get();
    assertTrue(facture.getEnvoyeParEmail());
    assertNotNull(facture.getDateEnvoiEmail());
}
```

## Performances

### Optimisations recommandées

1. **Génération asynchrone**: Pour de gros volumes, générer les PDFs de manière asynchrone
2. **Cache**: Mettre en cache les PDFs générés
3. **File d'attente**: Utiliser une file d'attente pour les emails

### Exemple d'envoi asynchrone

```java
@Async
public CompletableFuture<Void> envoyerFactureParEmailAsync(UUID factureId) {
    envoyerFactureParEmail(factureId);
    return CompletableFuture.completedFuture(null);
}
```

## Sécurité

### Bonnes pratiques

1. **Validation**: Toujours valider que l'utilisateur a les droits sur la facture
2. **Rate limiting**: Limiter le nombre d'emails envoyés par heure
3. **Sanitization**: Nettoyer les données avant insertion dans les templates

### Exemple de validation

```java
public void envoyerFactureParEmail(UUID factureId, UUID userId) {
    Facture facture = factureRepository.findById(factureId)
        .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée"));

    // Vérifier que l'utilisateur a accès à cette facture
    if (!facture.getUserId().equals(userId)) {
        throw new AccessDeniedException("Accès non autorisé");
    }

    // Procéder à l'envoi...
}
```

## Intégration avec Kafka

Les événements suivants sont publiés automatiquement:

- **Facture envoyée par email**: Event `facture-updated`
- **PDF généré**: Peut être ajouté comme event personnalisé

## Support

Pour toute question:
- Consulter les logs de l'application
- Vérifier la documentation Thymeleaf: https://www.thymeleaf.org/
- Vérifier la documentation OpenHtmlToPdf: https://github.com/danfickle/openhtmltopdf

## Améliorations Futures

1. **Templates multiples**: Différents templates selon le type de client
2. **Multilingue**: Support de plusieurs langues
3. **Signatures électroniques**: Ajout de signatures numériques aux PDFs
4. **Historique**: Garder un historique des emails envoyés
5. **Prévisualisation**: Prévisualiser le PDF avant envoi
