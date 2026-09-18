package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.domain.port.input.AuthUseCase;
import com.example.account.modules.facturation.dto.request.LoginRequest;
import com.example.account.modules.facturation.dto.request.MfaConfirmRequest;
import com.example.account.modules.facturation.dto.request.PinLoginRequest;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Seller authentication")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    @Operation(summary = "Seller login", description = "Returns full seller profile with org and agency details on success.")
    public Mono<ResponseEntity<SellerAuthResponse>> login(@RequestBody LoginRequest request) {
        log.info("Login request for username: {}", request.getUsername());
        return authUseCase.login(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/try-out")
    @Operation(summary = "Try Out login", description = "Authenticates directly against Kernel using the org the account already belongs to, auto-provisioning a local seller on first use. Does not create new organizations.")
    public Mono<ResponseEntity<SellerAuthResponse>> tryOut(@RequestBody LoginRequest request) {
        log.info("Try Out login request for principal: {}", request.getUsername());
        return authUseCase.tryOut(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/login/mfa")
    @Operation(summary = "Confirm MFA", description = "Second step of login/try-out: confirms the emailed OTP code with the mfaToken returned by /login or /try-out, and returns the same seller profile those would have returned directly.")
    public Mono<ResponseEntity<SellerAuthResponse>> confirmMfa(@Valid @RequestBody MfaConfirmRequest request) {
        log.info("MFA confirmation request");
        return authUseCase.confirmMfa(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/login/by_pin")
    @Operation(summary = "Seller PIN login (local)", description = "Quick POS-terminal login via organization + 5-digit PIN, resolved against Billing's own local SellerAuthContext store.")
    public Mono<ResponseEntity<SellerAuthResponse>> loginByLocalPin(@Valid @RequestBody PinLoginRequest request) {
        log.info("Local PIN login request for organization: {}", request.getOrganizationId());
        return authUseCase.loginByLocalPin(request)
                .map(ResponseEntity::ok);
    }
}
