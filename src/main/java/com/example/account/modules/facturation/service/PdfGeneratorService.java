package com.example.account.modules.facturation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class PdfGeneratorService {

    private final WebClient webClient;

    public PdfGeneratorService() {
        // Using Port 8081 as mapped in your Docker Compose
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8081") 
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024)) // Increase buffer to 16MB for larger PDFs
                .build();
    }

    /**
     * Converts HTML to PDF bytes using Gotenberg.
     * All processing happens in memory; no local files are created.
     */
    public byte[] generatePdfFromHtml(String html) {
        log.info("Requesting PDF bytes from Gotenberg Chromium engine...");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        
        // This 'index.html' is a VIRTUAL name sent in the HTTP request.
        // It never creates a real file on your Desktop or project folder.
        builder.part("files", new ByteArrayResource(html.getBytes(StandardCharsets.UTF_8)))
               .filename("index.html")
               .contentType(MediaType.TEXT_HTML);

        try {
            return webClient.post()
                    .uri("/forms/chromium/convert/html")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .doOnSuccess(s -> log.info("Successfully received PDF bytes (Size: {} bytes)", s.length))
                    .block(); // Blocking is okay here as it's handled by boundedElastic
        } catch (Exception e) {
            log.error("PDF generation failed: {}", e.getMessage());
            throw new RuntimeException("Could not generate PDF via Gotenberg", e);
        }
    }
}