package com.example.RHBackend.controllers;

import com.example.RHBackend.dtos.AuthResponse;
import com.example.RHBackend.dtos.LoginRequest;
import com.example.RHBackend.dtos.MeResponse;
import com.example.RHBackend.dtos.RefreshTokenRequest;
import com.example.RHBackend.models.User;
import com.example.RHBackend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Validated @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticate(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Validated @RequestBody RefreshTokenRequest refreshTokenRequest) {

        try {
            AuthResponse authResponse = authService.refreshToken(refreshTokenRequest);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException exception) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", exception.getMessage()));
        }

    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal User currentUser){
        if (currentUser ==null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message","non authentifier"));
        }
        MeResponse meResponse = new MeResponse();
        meResponse.setUserId(currentUser.getUserId());
        meResponse.setUsername(currentUser.getUsername());
        meResponse.setRole(currentUser.getRole());
        meResponse.setEmployeId(currentUser.getEmploye()
        !=null ?
                currentUser.getEmploye().getEmployeId() : null);

        return ResponseEntity.ok(meResponse);
    }
}

