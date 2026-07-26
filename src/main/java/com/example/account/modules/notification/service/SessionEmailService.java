package com.example.account.modules.notification.service;

import com.example.account.modules.notification.dto.SendSessionCreatedEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionEmailService {

    private final SpringTemplateEngine templateEngine;
    private final EmailSenderService emailSenderService;

    public Mono<Void> sendSessionCreated(SendSessionCreatedEmailRequest request) {
        return Mono.fromCallable(() -> {
            Context context = new Context();
            context.setVariable("username", request.getUsername());
            context.setVariable("agency", request.getAgency());
            context.setVariable("sessionType", request.getSessionType());
            context.setVariable("status", request.getStatus());
            context.setVariable("startTime", request.getStartTime());
            context.setVariable("openingAmount", request.getOpeningAmount());
            return templateEngine.process("session-created", context);
        }).subscribeOn(Schedulers.boundedElastic())
          .flatMap(html -> emailSenderService.sendEmail(request.getOrganizationId(), request.getEmail(),
                  "Votre session KSM a été créée", html))
          .doOnSuccess(v -> log.info("Sent session-created email to {}", request.getEmail()));
    }
}
