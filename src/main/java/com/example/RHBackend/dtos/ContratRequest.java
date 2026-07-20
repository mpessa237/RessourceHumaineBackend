package com.example.RHBackend.dtos;

import com.example.RHBackend.enums.TypeContrat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContratRequest {

    private Long employeId;

    private TypeContrat typeContrat;  // CDI, CDD, STAGE

    private LocalDate dateDebut;

    // Obligatoire uniquement pour CDD et STAGE
    private LocalDate dateFin;

    private BigDecimal salaire;

    private String observations;
}
