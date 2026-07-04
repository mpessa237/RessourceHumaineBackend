package com.example.RHBackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="postes")
public class Poste {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long posteId;

    @Column(nullable = false)
    private String intitule;

    @Column(name = "niveau_requis")
    private String niveauRequis;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "poste", fetch = FetchType.LAZY)
    private List<Employe> employes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
