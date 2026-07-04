package com.example.RHBackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartementResponse {
    private Long departementId;
    private String nom;
    private String description;
    private int nombreEmployes;
    private LocalDateTime createdAt;

}
