package com.example.RHBackend.dtos;

import com.example.RHBackend.enums.StatutPresence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresenceRequest {

    private Long employeId;

    private LocalDate date;

    private LocalTime heureArrivee;
    private LocalTime heureDepart;

    private StatutPresence statutPresence; // PRESENT, ABSENT, RETARD, CONGE, FERIE

    private String observations;
}
