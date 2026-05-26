package com.loki.tesis.auth.controller;

import com.loki.tesis.auth.dto.AccountResponseDTO;
import com.loki.tesis.auth.dto.RegisterRequestDTO;
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
}
