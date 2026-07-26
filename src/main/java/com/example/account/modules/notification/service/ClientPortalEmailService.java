package com.example.account.modules.notification.service;

import com.example.account.modules.notification.dto.SendClientPortalInvitationEmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class ClientPortalEmailService {

    private final SpringTemplateEngine templateEngine;
    private final EmailSenderService emailSenderService;

    @Value("${client-portal.invite.login-url}")
    private String loginUrl;

    public ClientPortalEmailService(SpringTemplateEngine templateEngine, EmailSenderService emailSenderService) {
        this.templateEngine = templateEngine;
        this.emailSenderService = emailSenderService;
    }

    public Mono<Void> sendInvitation(SendClientPortalInvitationEmailRequest request) {
        return Mono.fromCallable(() -> {
            Context context = new Context();
            context.setVariable("name", request.getName());
            context.setVariable("email", request.getEmail());
            context.setVariable("temporaryPassword", request.getTemporaryPassword());
            context.setVariable("loginUrl", loginUrl);
            return templateEngine.process("client-portal-invitation", context);
        }).subscribeOn(Schedulers.boundedElastic())
          .flatMap(html -> emailSenderService.sendEmail(request.getOrganizationId(), request.getEmail(),
                  "Bienvenue sur votre espace client — vos identifiants de connexion", html))
          .doOnSuccess(v -> log.info("Sent client portal invitation email to {}", request.getEmail()));
    }
}
