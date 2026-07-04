package com.example.RHBackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "soldes_conges",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"employe_id", "annee"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoldeConge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long soldeCongeId;
    @Column(nullable = false)
    private int annee;

    @Column(name = "jours_accordes", nullable = false)
    private int joursAccordes = 30;
    // Jours deja utilises (approuves)
    @Column(name = "jours_utilises", nullable = false)
    private int joursUtilises = 0;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public int getJoursRestants() {
        return joursAccordes - joursUtilises;
    }

}
