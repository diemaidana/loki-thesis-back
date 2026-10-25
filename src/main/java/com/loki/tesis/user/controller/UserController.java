package com.loki.tesis.user.controller;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.user.dto.UserResponseDTO;
import com.loki.tesis.user.dto.UserUpdateDTO;
import com.loki.tesis.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getUserProfile(@AuthenticationPrincipal Credential credential) {
        return ResponseEntity.ok(userService.getUserByUuid(credential.getUser().getUuid()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> updateUser(@AuthenticationPrincipal Credential credential,
                                                      @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserResponseDTO updatedUser = userService.updateUser(credential.getUser().getUuid(), userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal Credential credential) {
        userService.deleteUser(credential.getUser().getUuid());
        return ResponseEntity.noContent().build();
    }
}
