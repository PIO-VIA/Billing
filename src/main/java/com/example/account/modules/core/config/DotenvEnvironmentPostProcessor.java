package com.example.account.modules.core.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads KEY=VALUE pairs from a .env file in the working directory into the
 * Spring Environment, so ${KERNEL_API_KEY:default}-style placeholders in
 * application.properties resolve from .env instead of silently falling back
 * to the hardcoded literal — without requiring every developer to export
 * vars by hand before running the app. Registered via spring.factories
 * (EnvironmentPostProcessor), which runs before property placeholders are
 * resolved. Real OS environment variables and -D system properties still
 * take priority over .env, since those sit above it in Spring's property
 * source ordering.
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path envFile = Path.of(".env");
        if (!Files.isRegularFile(envFile)) {
            return;
        }

        Map<String, Object> values = new LinkedHashMap<>();
        try {
            for (String line : Files.readAllLines(envFile)) {
                String trimmed = line.strip();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eq = trimmed.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, eq).strip();
                String value = unquote(trimmed.substring(eq + 1).strip());
                values.put(key, value);
            }
        } catch (IOException e) {
            return;
        }

        if (!values.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource("dotenv", values));
        }
    }

    private String unquote(String value) {
        if (value.length() >= 2
                && (value.startsWith("\"") && value.endsWith("\"") || value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
