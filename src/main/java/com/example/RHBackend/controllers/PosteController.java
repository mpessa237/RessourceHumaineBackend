package com.example.RHBackend.controllers;

import com.example.RHBackend.dtos.PosteRequest;
import com.example.RHBackend.dtos.PosteResponse;
import com.example.RHBackend.services.PosteService;
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
@RequestMapping("/api/postes")
@PreAuthorize("hasRole('ADMIN')")
public class PosteController {

    private final PosteService posteService;

    @PostMapping
    public ResponseEntity<PosteResponse> creer(
            @Validated @RequestBody PosteRequest posteRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(posteService.creer(posteRequest));
    }

    @GetMapping
    public ResponseEntity<List<PosteResponse>> listerTous(){
        return ResponseEntity.ok(posteService.listerTous());
    }

    @GetMapping("/{posteId}")
    public ResponseEntity<PosteResponse> voirParId(
            @PathVariable Long posteId) {
        return ResponseEntity.ok(posteService.voirParId(posteId));
    }


    @PutMapping("/{posteId}")
    public ResponseEntity<PosteResponse> modifier(
            @PathVariable Long posteId,
            @Validated @RequestBody PosteRequest posteRequest) {
        return ResponseEntity.ok(
                posteService.modifier(posteId, posteRequest));
    }

    @DeleteMapping("/{posteId}")
    public ResponseEntity<?> supprimer(
            @PathVariable Long posteId) {
        try {
            posteService.supprimer(posteId);
            return ResponseEntity.ok(
                    Map.of("message",
                            "Poste supprime avec succes."));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

}
