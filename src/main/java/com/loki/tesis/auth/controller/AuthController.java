package com.loki.tesis.auth.controller;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.dto.response.AccountResponseDTO;
import com.loki.tesis.auth.dto.request.LoginRequestDTO;
import com.loki.tesis.auth.dto.response.LoginResponseDTO;
import com.loki.tesis.auth.dto.request.RegisterRequestDTO;
import com.loki.tesis.auth.dto.request.ResendVerificationRequestDTO;
import com.loki.tesis.auth.dto.request.VerifyEmailRequestDTO;
import com.loki.tesis.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<AccountResponseDTO> getCurrentUser(@AuthenticationPrincipal Credential credential) {
        AccountResponseDTO accountResponseDTO = authService.getCurrentUser(credential);
        return ResponseEntity.ok(accountResponseDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<AccountResponseDTO>  register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        AccountResponseDTO accountResponseDTO = authService.register(registerRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO);
        return ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequestDTO verifyEmailRequestDTO) {
        authService.verifyEmail(verifyEmailRequestDTO.token());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/verify/request")
    public ResponseEntity<Void> resendEmailVerification(@Valid @RequestBody ResendVerificationRequestDTO resendVerificationRequestDTO) {
        authService.resendEmailVerification(resendVerificationRequestDTO.email());
        return ResponseEntity.noContent().build();
    }
}
