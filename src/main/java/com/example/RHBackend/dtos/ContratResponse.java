package com.example.RHBackend.dtos;

import com.example.RHBackend.enums.TypeContrat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContratResponse {

    private Long contratId;
    private String employeNom;
    private String employePrenom;
    private String employeMatricule;
    private TypeContrat typeContrat;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal salaire;
    private String observations;
    private Boolean estActif;
    private LocalDateTime createdAt;
}
