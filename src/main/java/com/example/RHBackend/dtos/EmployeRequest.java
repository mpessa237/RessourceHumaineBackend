package com.example.RHBackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeRequest {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateEmbauche;
    private Long departementId;
    private Long posteId;

    //mdp par default
    private String password;
}
