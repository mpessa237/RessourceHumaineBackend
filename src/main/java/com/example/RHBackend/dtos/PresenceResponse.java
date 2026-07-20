package com.example.RHBackend.dtos;

import com.example.RHBackend.enums.StatutPresence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresenceResponse {

    private Long id;
    private Long employeId;
    private String employeNom;
    private String employePrenom;
    private String employeMatricule;
    private LocalDate date;
    private LocalTime heureArrivee;
    private LocalTime heureDepart;
    private double dureeHeures;
    private StatutPresence statutPresence;
    private String observations;
    private LocalDateTime createdAt;
}
