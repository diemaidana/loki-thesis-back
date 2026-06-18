package com.loki.tesis.user.controller;

import com.loki.tesis.user.dto.UserResponseDTO;
import com.loki.tesis.user.dto.UserUpdateDTO;
import com.loki.tesis.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me") // Esto es una prueba a ver si funcionaba Postman (resultado? NO funciona)
    public ResponseEntity<String> getUsers() {
        return ResponseEntity.ok("Hello world. El endpoint funciona bien.");
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID uuid, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        UserResponseDTO updatedUser = userService.updateUser(uuid, userUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID uuid) {
        userService.deleteUser(uuid);
        return ResponseEntity.noContent().build();
    }
}
