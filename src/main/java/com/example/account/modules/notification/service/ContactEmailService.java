package com.example.account.modules.notification.service;

import com.example.account.modules.notification.dto.SendContactInvitationEmailRequest;
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
public class ContactEmailService {

    private final SpringTemplateEngine templateEngine;
    private final EmailSenderService emailSenderService;

    public Mono<Void> sendInvitation(SendContactInvitationEmailRequest request) {
        return Mono.fromCallable(() -> {
            Context context = new Context();
            context.setVariable("name", request.getName());
            context.setVariable("telegramDeepLink", request.getTelegramDeepLink());
            return templateEngine.process("contact-invitation", context);
        }).subscribeOn(Schedulers.boundedElastic())
          .flatMap(html -> emailSenderService.sendEmail(request.getOrganizationId(), request.getEmail(),
                  "Activez vos notifications KSM sur Telegram", html))
          .doOnSuccess(v -> log.info("Sent Telegram invite email to {}", request.getEmail()));
    }
}
