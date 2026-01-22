package com.example.account.modules.core.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic factureCreatedTopic() {
        return TopicBuilder.name("facture-created").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic factureUpdatedTopic() {
        return TopicBuilder.name("facture-updated").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic factureDeletedTopic() {
        return TopicBuilder.name("facture-deleted").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic facturePaidTopic() {
        return TopicBuilder.name("facture-paid").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic devisCreatedTopic() {
        return TopicBuilder.name("devis-created").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic devisUpdatedTopic() {
        return TopicBuilder.name("devis-updated").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic paiementCreatedTopic() {
        return TopicBuilder.name("paiement-created").partitions(1).replicas(1).build();
    }
}
