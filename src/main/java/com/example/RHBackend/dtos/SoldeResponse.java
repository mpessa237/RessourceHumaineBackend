package com.example.RHBackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoldeResponse {
    private Long employeId;
    private String employeNom;
    private int annee;
    private int joursAccordes;
    private int joursUtilises;
    private int joursRestants;

}
