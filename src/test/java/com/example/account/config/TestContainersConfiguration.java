package com.example.account.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Configuration TestContainers pour les tests d'intégration
 *
 * Cette configuration démarre automatiquement :
 * - Un conteneur PostgreSQL pour la base de données
 * - Un conteneur Kafka pour les messages
 *
 * Les conteneurs sont réutilisés entre les tests pour améliorer les performances.
 * Ils sont automatiquement arrêtés à la fin de tous les tests.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    /**
     * Container PostgreSQL pour les tests
     *
     * Version : PostgreSQL 16 (Alpine pour image légère)
     * Database : testdb
     * Username : test
     * Password : test
     *
     * @ServiceConnection crée automatiquement les propriétés Spring pour la connexion
     */
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);  // Réutilise le conteneur entre les tests
    }

    /**
     * Container Kafka pour les tests
     *
     * Version : Confluent Platform 7.5.0
     *
     * @ServiceConnection configure automatiquement spring.kafka.bootstrap-servers
     */
    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))
                .withReuse(true);  // Réutilise le conteneur entre les tests
    }
}
