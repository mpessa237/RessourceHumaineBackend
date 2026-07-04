package com.example.RHBackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PosteRequest {
    private String intitule;
    private String niveauRequis;
    private String description;

}
