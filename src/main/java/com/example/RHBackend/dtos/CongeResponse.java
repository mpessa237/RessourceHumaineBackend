package com.example.RHBackend.dtos;

import com.example.RHBackend.enums.StatutConge;
import com.example.RHBackend.enums.TypeConge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CongeResponse {
    private Long id;
    private String employeNom;
    private String employePrenom;
    private String employeMatricule;
    private TypeConge typeConge;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private long nombreJours;
    private String motif;
    private StatutConge statutConge ;
    private String commentaireRh;
    private String validateurUsername;
    private LocalDateTime dateDecision;
    private LocalDateTime createdAt;

}
