package com.example.RHBackend.controllers;

import com.example.RHBackend.dtos.ContratRequest;
import com.example.RHBackend.dtos.ContratResponse;
import com.example.RHBackend.services.ContratService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contrats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ContratController {

    private final ContratService contratService;

    @PostMapping
    public ResponseEntity<ContratResponse> creer(
            @Validated @RequestBody ContratRequest contratRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contratService.creer(contratRequest));
    }

    @GetMapping
    public ResponseEntity<List<ContratResponse>> getAll(){
        return ResponseEntity.ok(contratService.listerTous());
    }

    @GetMapping("/{contratId}")
    public ResponseEntity<ContratResponse> getById(@PathVariable Long contratId){
        return ResponseEntity.ok(contratService.voirParId(contratId));
    }
}
