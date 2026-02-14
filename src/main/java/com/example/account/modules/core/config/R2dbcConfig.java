package com.example.account.modules.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * R2DBC Configuration for reactive database access.
 * Configures PostgreSQL connection factory, transaction manager, and custom converters.
 */
@Configuration
@EnableR2dbcRepositories(basePackages = "com.example.account.modules")
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.url}")
    private String r2dbcUrl;

    @Value("${spring.r2dbc.username}")
    private String username;

    @Value("${spring.r2dbc.password}")
    private String password;

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        // Extract host, port, and database from r2dbc URL
        // Format: r2dbc:postgresql://localhost:5432/billing
        String[] parts = r2dbcUrl.replace("r2dbc:postgresql://", "").split("/");
        String[] hostPort = parts[0].split(":");
        String host = hostPort[0];
        int port = Integer.parseInt(hostPort[1]);
        String database = parts[1];

        return new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .username(username)
                .password(password)
                .build()
        );
    }

    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    /**
     * Custom converters for handling JSONB types and other PostgreSQL-specific types.
     */
    @Override
    protected List<Object> getCustomConverters() {
        List<Object> converters = new ArrayList<>();
        converters.add(new JsonbToListConverter());
        converters.add(new ListToJsonbConverter());
        return converters;
    }

    /**
     * Converter to read JSONB from PostgreSQL to Java List.
     */
    @ReadingConverter
    public static class JsonbToListConverter implements Converter<Json, List> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public List convert(Json json) {
            try {
                return objectMapper.readValue(json.asString(), List.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to convert JSONB to List", e);
            }
        }
    }

    /**
     * Converter to write Java List to JSONB for PostgreSQL.
     */
    @WritingConverter
    public static class ListToJsonbConverter implements Converter<List, Json> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public Json convert(List list) {
            try {
                return Json.of(objectMapper.writeValueAsString(list));
            } catch (Exception e) {
                throw new RuntimeException("Failed to convert List to JSONB", e);
            }
        }
    }
}
