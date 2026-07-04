package com.example.RHBackend.controllers;

import com.example.RHBackend.dtos.EmployeRequest;
import com.example.RHBackend.dtos.EmployeResponse;
import com.example.RHBackend.enums.StatutEmploye;
import com.example.RHBackend.services.EmployeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EmployeController {

    private final EmployeService employeService;

    @PostMapping
    public ResponseEntity<EmployeResponse> creer(
            @Validated @RequestBody EmployeRequest employeRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeService.creer(employeRequest));
    }

    @GetMapping
    public ResponseEntity<List<EmployeResponse>> listerAll(){
        return ResponseEntity.ok(employeService.listerAll());
    }

    @GetMapping("/{employeId}")
    public ResponseEntity<EmployeResponse> voirParId(@PathVariable Long employeId) {
        return ResponseEntity.ok(employeService.voirParId(employeId));
    }

    // GET /api/employes/search?q=jean
    // Recherche par nom, prenom ou matricule
    @GetMapping("/search")
    public ResponseEntity<List<EmployeResponse>> rechercher(
            @RequestParam(defaultValue = "") String q) {
        return ResponseEntity.ok(employeService.rechercher(q));
    }


    @PutMapping("/{employeId}")
    public ResponseEntity<EmployeResponse> modifier(@PathVariable Long employeId, @Validated @RequestBody EmployeRequest employeRequest) {
        return ResponseEntity.ok(
                employeService.modifier(employeId, employeRequest));
    }

    // Body : { "statut": "INACTIF" }
    @PatchMapping("/{employeId}/statut")
    public ResponseEntity<EmployeResponse> changerStatut(
            @PathVariable Long employeId,
            @RequestBody Map<String, String> body) {
        StatutEmploye statutEmploye = StatutEmploye
                .valueOf(body.get("statutEmploye"));
        return ResponseEntity.ok(
                employeService.changerStatut(employeId, statutEmploye));
    }


}
