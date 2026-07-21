package com.example.RHBackend.controllers;

import com.example.RHBackend.dtos.PresenceRequest;
import com.example.RHBackend.dtos.PresenceResponse;
import com.example.RHBackend.services.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RH')")
    public ResponseEntity<List<PresenceResponse>> getAll(){
        return ResponseEntity.ok(presenceService.listerToutes());
    }

    @GetMapping("/{employeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RH', 'EMPLOYE')")
    public ResponseEntity<List<PresenceResponse>> getById(@PathVariable Long employeId){
        return ResponseEntity.ok(presenceService.voirParEmploye(employeId));
    }

    // GET /api/presences/date/{date} — ADMIN + RH
    // Format date : 2025-08-01
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RH')")
    public ResponseEntity<List<PresenceResponse>> voirParDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return ResponseEntity.ok(presenceService.voirParDate(date));
    }

    // PUT /api/presences/{id} — ADMIN + RH
    @PutMapping("/{employeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RH')")
    public ResponseEntity<PresenceResponse> modifier(
            @PathVariable Long employeId,
            @Validated @RequestBody PresenceRequest presenceRequest) {
        return ResponseEntity.ok(presenceService.modifier(employeId, presenceRequest));
    }
}
