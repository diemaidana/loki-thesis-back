package com.loki.tesis.auth.controller;

import com.loki.tesis.auth.dto.AccountResponseDTO;
import com.loki.tesis.auth.dto.RegisterRequestDTO;
import com.loki.tesis.auth.dto.ResendVerificationRequestDTO;
import com.loki.tesis.auth.dto.VerifyEmailRequestDTO;
import com.loki.tesis.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    // TODO: Implementar con @AuthenticationPrincipal cuando este Spring Security
    // TODO: BORRAR REQUESTPARAM.
    @GetMapping("/me")
    public ResponseEntity<AccountResponseDTO> getCurrentUser(@RequestParam @NotBlank @Email String email) {
        AccountResponseDTO accountResponseDTO = authService.getCurrentUser(email);
        return ResponseEntity.ok(accountResponseDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<AccountResponseDTO>  register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        AccountResponseDTO accountResponseDTO = authService.register(registerRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDTO);
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
