package com.example.account.modules.notification.controller;

import com.example.account.modules.notification.dto.SendClientPortalInvitationEmailRequest;
import com.example.account.modules.notification.dto.SendContactInvitationEmailRequest;
import com.example.account.modules.notification.dto.SendSellerInvitationEmailRequest;
import com.example.account.modules.notification.dto.SendSessionCreatedEmailRequest;
import com.example.account.modules.notification.service.ClientPortalEmailService;
import com.example.account.modules.notification.service.ContactEmailService;
import com.example.account.modules.notification.service.SellerEmailService;
import com.example.account.modules.notification.service.SessionEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Internal endpoints called by sales-core (and any other satellite) for the
 * domain-specific transactional emails that used to be built and sent locally
 * in each caller. Billing owns the templates and the actual Kernel
 * notification-core dispatch; callers just send the fields needed to render.
 */
@RestController
@RequestMapping("/api/notifications/email")
@RequiredArgsConstructor
@Tag(name = "Internal Email Notifications", description = "Domain-specific transactional emails for satellite services")
public class EmailNotificationController {

    private final SellerEmailService sellerEmailService;
    private final SessionEmailService sessionEmailService;
    private final ContactEmailService contactEmailService;
    private final ClientPortalEmailService clientPortalEmailService;

    @PostMapping("/seller-invitation")
    @Operation(summary = "Send a seller invitation email with temporary credentials")
    public Mono<Void> sendSellerInvitation(@Valid @RequestBody SendSellerInvitationEmailRequest request) {
        return sellerEmailService.sendInvitation(request);
    }

    @PostMapping("/session-created")
    @Operation(summary = "Notify a seller that their POS session was created")
    public Mono<Void> sendSessionCreated(@Valid @RequestBody SendSessionCreatedEmailRequest request) {
        return sessionEmailService.sendSessionCreated(request);
    }

    @PostMapping("/contact-invitation")
    @Operation(summary = "Invite a contact to activate Telegram notifications")
    public Mono<Void> sendContactInvitation(@Valid @RequestBody SendContactInvitationEmailRequest request) {
        return contactEmailService.sendInvitation(request);
    }

    @PostMapping("/client-portal-invitation")
    @Operation(summary = "Send client-portal login credentials to a new account")
    public Mono<Void> sendClientPortalInvitation(@Valid @RequestBody SendClientPortalInvitationEmailRequest request) {
        return clientPortalEmailService.sendInvitation(request);
    }
}
