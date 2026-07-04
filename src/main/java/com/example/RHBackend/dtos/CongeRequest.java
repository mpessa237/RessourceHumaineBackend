package com.example.RHBackend.dtos;

import com.example.RHBackend.enums.TypeConge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CongeRequest {
    private TypeConge typeConge;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;
}
