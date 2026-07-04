package com.example.RHBackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PosteResponse {
    private Long posteId;
    private String intitule;
    private String niveauRequis;
    private String description;
    private int nombreEmployes;
    private LocalDateTime createdAt;

}
