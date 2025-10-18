package com.example.account.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    // Topics pour les clients
    @Bean
    public NewTopic clientCreatedTopic() {
        return TopicBuilder.name("client-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic clientUpdatedTopic() {
        return TopicBuilder.name("client-updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic clientDeletedTopic() {
        return TopicBuilder.name("client-deleted")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Topics pour les factures
    @Bean
    public NewTopic factureCreatedTopic() {
        return TopicBuilder.name("facture-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic factureUpdatedTopic() {
        return TopicBuilder.name("facture-updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic factureDeletedTopic() {
        return TopicBuilder.name("facture-deleted")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic facturePaidTopic() {
        return TopicBuilder.name("facture-paid")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Topics pour les devis
    @Bean
    public NewTopic devisCreatedTopic() {
        return TopicBuilder.name("devis-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic devisUpdatedTopic() {
        return TopicBuilder.name("devis-updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic devisDeletedTopic() {
        return TopicBuilder.name("devis-deleted")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic devisAcceptedTopic() {
        return TopicBuilder.name("devis-accepted")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Topics pour les paiements
    @Bean
    public NewTopic paiementCreatedTopic() {
        return TopicBuilder.name("paiement-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paiementUpdatedTopic() {
        return TopicBuilder.name("paiement-updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paiementDeletedTopic() {
        return TopicBuilder.name("paiement-deleted")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Topics pour les produits
    @Bean
    public NewTopic produitCreatedTopic() {
        return TopicBuilder.name("produit-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic produitUpdatedTopic() {
        return TopicBuilder.name("produit-updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic produitDeletedTopic() {
        return TopicBuilder.name("produit-deleted")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Topics pour les fournisseurs
    @Bean
    public NewTopic fournisseurCreatedTopic() {
        return TopicBuilder.name("fournisseur-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic fournisseurUpdatedTopic() {
        return TopicBuilder.name("fournisseur-updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic fournisseurDeletedTopic() {
        return TopicBuilder.name("fournisseur-deleted")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Topics pour les notifications
    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name("notification")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Topics pour les événements d'audit
    @Bean
    public NewTopic auditTopic() {
        return TopicBuilder.name("audit")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
