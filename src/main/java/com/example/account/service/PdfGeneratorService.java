package com.example.account.service;

import com.example.account.model.entity.Client;
import com.example.account.model.entity.Facture;
import com.example.account.model.entity.Paiement;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGeneratorService {

    private final TemplateEngine templateEngine;
    private static final String PDF_OUTPUT_DIR = "pdfs/factures/";
    private static final String PDF_PAIEMENT_DIR = "pdfs/paiements/";

    /**
     * Génère un PDF de facture à partir du template Thymeleaf
     */
    public byte[] generateFacturePdf(Facture facture, Client client) {
        log.info("Génération du PDF pour la facture: {}", facture.getNumeroFacture());

        try {
            // Préparer le contexte Thymeleaf
            Context context = new Context(Locale.FRENCH);
            context.setVariable("facture", facture);
            context.setVariable("client", client);
            context.setVariable("lignesFacture", facture.getLignesFacture());
            context.setVariable("logoPath", ""); // Ajouter le chemin du logo si nécessaire

            // Générer le HTML à partir du template
            String htmlContent = templateEngine.process("pdf/ facture-template", context);

            // Convertir HTML en PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, "");
            builder.toStream(outputStream);
            builder.run();

            byte[] pdfBytes = outputStream.toByteArray();
            log.info("PDF de facture généré avec succès: {} ({} bytes)", facture.getNumeroFacture(), pdfBytes.length);

            return pdfBytes;

        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF de facture {}: {}", facture.getNumeroFacture(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du PDF de facture", e);
        }
    }

    /**
     * Génère et sauvegarde un PDF de facture sur le disque
     */
    public String generateAndSaveFacturePdf(Facture facture, Client client) {
        log.info("Génération et sauvegarde du PDF pour la facture: {}", facture.getNumeroFacture());

        try {
            // Créer le répertoire si nécessaire
            Path outputPath = Paths.get(PDF_OUTPUT_DIR);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            // Générer le PDF
            byte[] pdfBytes = generateFacturePdf(facture, client);

            // Créer le nom du fichier
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("facture_%s_%s.pdf", facture.getNumeroFacture(), timestamp);
            String filepath = PDF_OUTPUT_DIR + filename;

            // Sauvegarder le fichier
            try (FileOutputStream fos = new FileOutputStream(filepath)) {
                fos.write(pdfBytes);
            }

            log.info("PDF de facture sauvegardé: {}", filepath);
            return filepath;

        } catch (IOException e) {
            log.error("Erreur lors de la sauvegarde du PDF de facture {}: {}", facture.getNumeroFacture(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde du PDF de facture", e);
        }
    }

    /**
     * Génère un PDF de reçu de paiement
     */
    public byte[] generateRecuPaiementPdf(Paiement paiement, Facture facture, Client client) {
        log.info("Génération du PDF de reçu pour le paiement: {}", paiement.getIdPaiement());

        try {
            // Préparer le contexte Thymeleaf
            Context context = new Context(Locale.FRENCH);
            context.setVariable("paiement", paiement);
            context.setVariable("facture", facture);
            context.setVariable("client", client);
            context.setVariable("logoPath", "");

            // Générer le HTML à partir du template
            String htmlContent = templateEngine.process("pdf/recu-paiement", context);

            // Convertir HTML en PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, "");
            builder.toStream(outputStream);
            builder.run();

            byte[] pdfBytes = outputStream.toByteArray();
            log.info("PDF de reçu de paiement généré avec succès: {} ({} bytes)", paiement.getIdPaiement(), pdfBytes.length);

            return pdfBytes;

        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF de reçu de paiement {}: {}", paiement.getIdPaiement(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du PDF de reçu de paiement", e);
        }
    }

    /**
     * Génère et sauvegarde un PDF de reçu de paiement
     */
    public String generateAndSaveRecuPaiementPdf(Paiement paiement, Facture facture, Client client) {
        log.info("Génération et sauvegarde du PDF de reçu pour le paiement: {}", paiement.getIdPaiement());

        try {
            // Créer le répertoire si nécessaire
            Path outputPath = Paths.get(PDF_PAIEMENT_DIR);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            // Générer le PDF
            byte[] pdfBytes = generateRecuPaiementPdf(paiement, facture, client);

            // Créer le nom du fichier
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("recu_paiement_%s_%s.pdf", paiement.getIdPaiement(), timestamp);
            String filepath = PDF_PAIEMENT_DIR + filename;

            // Sauvegarder le fichier
            try (FileOutputStream fos = new FileOutputStream(filepath)) {
                fos.write(pdfBytes);
            }

            log.info("PDF de reçu de paiement sauvegardé: {}", filepath);
            return filepath;

        } catch (IOException e) {
            log.error("Erreur lors de la sauvegarde du PDF de reçu de paiement {}: {}", paiement.getIdPaiement(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde du PDF de reçu de paiement", e);
        }
    }

    /**
     * Supprime un fichier PDF
     */
    public boolean deletePdf(String filepath) {
        try {
            File file = new File(filepath);
            if (file.exists()) {
                boolean deleted = file.delete();
                log.info("PDF supprimé: {} - Succès: {}", filepath, deleted);
                return deleted;
            }
            return false;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du PDF {}: {}", filepath, e.getMessage());
            return false;
        }
    }
}
