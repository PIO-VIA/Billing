package com.example.account.modules.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger/OpenAPI pour la documentation des APIs.
 * 
 * Cette configuration permet de :
 * - Définir les informations de l'API (titre, version, description)
 * - Configurer plusieurs serveurs (local, dev, staging, production)
 * - Ajouter des informations de contact et de licence
 * 
 * Pour ajouter un nouveau serveur, ajoutez simplement une nouvelle entrée dans la méthode createServers().
 */
@Configuration
public class SwaggerConfig {

    @Value("${app.name:API Facturation & Multi-Tenancy}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.description:Documentation des APIs pour le module de facturation et la gestion des organisations}")
    private String appDescription;

    @Value("${app.contact.name:Support Technique}")
    private String contactName;

    @Value("${app.contact.email:support@example.com}")
    private String contactEmail;

    @Value("${app.contact.url:https://example.com}")
    private String contactUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServers());
    }

    /**
     * Crée les informations de l'API (titre, version, description, contact, licence).
     */
    private Info createApiInfo() {
        return new Info()
                .title(appName)
                .version(appVersion)
                .description(appDescription)
                .contact(createContact())
                .license(createLicense());
    }

    /**
     * Crée les informations de contact.
     */
    private Contact createContact() {
        return new Contact()
                .name(contactName)
                .email(contactEmail)
                .url(contactUrl);
    }

    /**
     * Crée les informations de licence.
     */
    private License createLicense() {
        return new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");
    }

    /**
     * Configure les serveurs disponibles pour tester l'API.
     * 
     * Pour ajouter un nouveau serveur :
     * 1. Créez une nouvelle instance de Server()
     * 2. Définissez l'URL avec .url()
     * 3. Ajoutez une description avec .description()
     * 4. Ajoutez-le à la liste retournée
     * 
     * Exemple :
     * Server newServer = new Server()
     *     .url("https://api.example.com")
     *     .description("Production Server");
     */
    private List<Server> createServers() {
        // Serveur local (développement)
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Serveur Local (Développement)");

        // Serveur de production
        Server productionServer = new Server()
                .url("https://billing-f6l8.onrender.com")
                .description("Serveur de Production");

        // Retourner la liste des serveurs
        // Note: Vous pouvez commenter/décommenter les serveurs selon vos besoins
        return List.of(
                localServer,
                productionServer
        );
    }
}
