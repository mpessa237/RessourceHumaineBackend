package com.example.RHBackend.dtos;

import com.example.RHBackend.enums.StatutEmploye;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeResponse {
    private String employeId;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String dateEmbauche;
    private StatutEmploye statutEmploye;
    private String departementNom;
    private String posteIntitule;
    private Long userId;
    private LocalDateTime createdAt;

    private String motDePasseInitial;
}
