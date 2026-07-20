package com.example.RHBackend.controllers;

import com.example.RHBackend.dtos.CongeRequest;
import com.example.RHBackend.dtos.CongeResponse;
import com.example.RHBackend.dtos.RejetRequest;
import com.example.RHBackend.models.User;
import com.example.RHBackend.services.CongeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conges")
public class CongeController {

    private final CongeService congeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYE', 'ADMIN', 'RH')")
    public ResponseEntity<CongeResponse> soumettre(
            @Validated @RequestBody CongeRequest congeRequest,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(congeService.soumettre(congeRequest, currentUser.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RH')")
    public ResponseEntity<List<CongeResponse>> listerTous() {
        return ResponseEntity.ok(congeService.listerTous());
    }

    @GetMapping("/{congeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RH')")
    public ResponseEntity<CongeResponse> voirParId(@PathVariable Long congeId) {
        return ResponseEntity.ok(congeService.voirParId(congeId));
    }

    // PATCH /api/conges/{id}/approuver
    @PatchMapping("/{congeId}/approuver")
    @PreAuthorize("hasAnyRole('ADMIN', 'RH')")
    public ResponseEntity<CongeResponse> approuver(
            @PathVariable Long congeId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                congeService.approuver(congeId, currentUser.getUsername()));
    }

    // PATCH /api/conges/{id}/rejeter
    @PatchMapping("/{congeId}/rejeter")
    @PreAuthorize("hasAnyRole('ADMIN', 'RH')")
    public ResponseEntity<CongeResponse> rejeter(
            @PathVariable Long congeId,
            @Validated @RequestBody RejetRequest rejetRequest ,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                congeService.rejeter(congeId, rejetRequest, currentUser.getUsername()));
    }
}
