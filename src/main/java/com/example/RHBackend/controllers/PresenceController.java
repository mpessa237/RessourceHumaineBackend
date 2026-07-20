package com.example.RHBackend.controllers;

import com.example.RHBackend.dtos.PresenceRequest;
import com.example.RHBackend.dtos.PresenceResponse;
import com.example.RHBackend.services.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/presences")
public class PresenceController {

    private final PresenceService presenceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RH')")
    public ResponseEntity<PresenceResponse> pointer(
             @RequestBody PresenceRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(presenceService.pointer(request));
    }
}
