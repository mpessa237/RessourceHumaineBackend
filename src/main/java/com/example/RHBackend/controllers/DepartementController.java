package com.example.RHBackend.controllers;

import com.example.RHBackend.dtos.DepartementRequest;
import com.example.RHBackend.dtos.DepartementResponse;
import com.example.RHBackend.services.DepartementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departements")
@PreAuthorize("hasRole('ADMIN')")
public class DepartementController {

    private final DepartementService departementService;

    @PostMapping
    public ResponseEntity<DepartementResponse> creer(
            @Validated @RequestBody DepartementRequest departementRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(departementService.creer(departementRequest));
    }

    @GetMapping
    public ResponseEntity<List<DepartementResponse>> listerTous(){
        return ResponseEntity.ok(departementService.litserTous());
    }

    @GetMapping("/{departementId}")
    public ResponseEntity<DepartementResponse> voirParId(
            @PathVariable Long departementId) {
        return ResponseEntity.ok(departementService.voirParId(departementId));
    }


    @PutMapping("/{departementId}")
    public ResponseEntity<DepartementResponse> modifier(
            @PathVariable Long departementId,
            @Validated @RequestBody DepartementRequest departementRequest) {
        return ResponseEntity.ok(
                departementService.modifier(departementId, departementRequest));
    }

    @DeleteMapping("/{departementId}")
    public ResponseEntity<?> supprimer(
            @PathVariable Long departementId) {
        try {
            departementService.supprimer(departementId);
            return ResponseEntity.ok(
                    Map.of("message",
                            "Departement supprime avec succes."));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

}
