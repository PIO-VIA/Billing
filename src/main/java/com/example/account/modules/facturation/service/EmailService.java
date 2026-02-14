package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.model.entity.Facture;
import com.example.account.modules.facturation.model.entity.Paiement;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:contact@yooyob.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Envoie un email de création de facture au client
     */
    public Mono<Void> sendFactureCreationEmail(Facture facture, String toEmail, byte[] pdfAttachment) {
        return Mono.fromRunnable(() -> {
            log.info("Envoi de l'email de création de facture {} à {}", facture.getNumeroFacture(), toEmail);

            try {
                // Préparer le contexte Thymeleaf
                Context context = new Context(Locale.FRENCH);
                context.setVariable("client", facture.getNomClient());
                context.setVariable("numeroFacture", facture.getNumeroFacture());
                context.setVariable("dateFacturation", facture.getDateFacturation());
                context.setVariable("dateEcheance", facture.getDateEcheance());
                context.setVariable("montantTotal", facture.getMontantTotal());
                context.setVariable("facture", facture);
                context.setVariable("baseUrl", baseUrl);

                // Générer le contenu HTML de l'email
                String htmlContent = templateEngine.process("email/facture-creation", context);

                // Créer et envoyer l'email
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(fromEmail);
                helper.setTo(toEmail);
                helper.setSubject("Nouvelle facture " + facture.getNumeroFacture());
                helper.setText(htmlContent, true);

                // Attacher le PDF si fourni
                if (pdfAttachment != null && pdfAttachment.length > 0) {
                    String filename = "Facture_" + facture.getNumeroFacture() + ".pdf";
                    helper.addAttachment(filename, new ByteArrayResource(pdfAttachment));
                }

                mailSender.send(message);
                log.info("Email de facture envoyé avec succès à {}", toEmail);

            } catch (MessagingException e) {
                log.error("Erreur lors de l'envoi de l'email de facture à {}: {}", toEmail, e.getMessage(), e);
                throw new RuntimeException("Erreur lors de l'envoi de l'email de facture", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    /**
     * Envoie un email de rappel de paiement
     */
    public Mono<Void> sendRappelPaiementEmail(Facture facture, String toEmail) {
        return Mono.fromRunnable(() -> {
            log.info("Envoi de l'email de rappel de paiement pour facture {} à {}", facture.getNumeroFacture(), toEmail);

            try {
                // Préparer le contexte Thymeleaf
                Context context = new Context(Locale.FRENCH);
                context.setVariable("client", facture.getNomClient());
                context.setVariable("numeroFacture", facture.getNumeroFacture());
                context.setVariable("dateFacturation", facture.getDateFacturation());
                context.setVariable("dateEcheance", facture.getDateEcheance());
                context.setVariable("montantRestant", facture.getMontantRestant());
                context.setVariable("facture", facture);
                context.setVariable("baseUrl", baseUrl);

                // Générer le contenu HTML de l'email
                String htmlContent = templateEngine.process("email/rappel-paiement", context);

                // Créer et envoyer l'email
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(fromEmail);
                helper.setTo(toEmail);
                helper.setSubject("Rappel de paiement - Facture " + facture.getNumeroFacture());
                helper.setText(htmlContent, true);

                mailSender.send(message);
                log.info("Email de rappel de paiement envoyé avec succès à {}", toEmail);

            } catch (MessagingException e) {
                log.error("Erreur lors de l'envoi de l'email de rappel à {}: {}", toEmail, e.getMessage(), e);
                throw new RuntimeException("Erreur lors de l'envoi de l'email de rappel", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    /**
     * Envoie un email de confirmation de paiement reçu
     */
    public Mono<Void> sendPaiementRecuEmail(Paiement paiement, Facture facture, String toEmail, byte[] recuPdf) {
        return Mono.fromRunnable(() -> {
            log.info("Envoi de l'email de paiement reçu pour {} à {}", paiement.getIdPaiement(), toEmail);

            try {
                // Préparer le contexte Thymeleaf
                Context context = new Context(Locale.FRENCH);
                context.setVariable("client", facture.getNomClient());
                context.setVariable("numeroFacture", facture.getNumeroFacture());
                context.setVariable("montantPaye", paiement.getMontant());
                context.setVariable("datePaiement", paiement.getDate());
                context.setVariable("modePaiement", paiement.getModePaiement());
                context.setVariable("montantRestant", facture.getMontantRestant());
                context.setVariable("paiement", paiement);
                context.setVariable("facture", facture);
                context.setVariable("baseUrl", baseUrl);

                // Générer le contenu HTML de l'email
                String htmlContent = templateEngine.process("email/paiement-recu", context);

                // Créer et envoyer l'email
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(fromEmail);
                helper.setTo(toEmail);
                helper.setSubject("Paiement reçu - Facture " + facture.getNumeroFacture());
                helper.setText(htmlContent, true);

                // Attacher le reçu PDF si fourni
                if (recuPdf != null && recuPdf.length > 0) {
                    String filename = "Recu_Paiement_" + paiement.getIdPaiement() + ".pdf";
                    helper.addAttachment(filename, new ByteArrayResource(recuPdf));
                }

                mailSender.send(message);
                log.info("Email de paiement reçu envoyé avec succès à {}", toEmail);

            } catch (MessagingException e) {
                log.error("Erreur lors de l'envoi de l'email de paiement reçu à {}: {}", toEmail, e.getMessage(), e);
                throw new RuntimeException("Erreur lors de l'envoi de l'email de paiement reçu", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    /**
     * Envoie un email simple (générique)
     */
    public Mono<Void> sendSimpleEmail(String to, String subject, String text) {
        return Mono.fromRunnable(() -> {
            log.info("Envoi d'un email simple à {}", to);

            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

                helper.setFrom(fromEmail);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(text, false);

                mailSender.send(message);
                log.info("Email simple envoyé avec succès à {}", to);

            } catch (MessagingException e) {
                log.error("Erreur lors de l'envoi de l'email à {}: {}", to, e.getMessage(), e);
                throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
