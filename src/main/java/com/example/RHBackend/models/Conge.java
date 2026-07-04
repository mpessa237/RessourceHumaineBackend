package com.example.RHBackend.models;

import com.example.RHBackend.enums.StatutConge;
import com.example.RHBackend.enums.TypeConge;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="conges")
public class Conge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long congeId;

    @Enumerated(EnumType.STRING)
    private TypeConge typeConge;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @Enumerated(EnumType.STRING)
    private StatutConge statutConge = StatutConge.EN_ATTENTE;

    @Column(name = "commentaire_rh", columnDefinition = "TEXT")
    private String commentaireRh;

    @Column(name = "date_decision")
    private LocalDateTime dateDecision;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validateur_id")
    private User validateur;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Calcul automatique du nombre de jours
    public long getNombreJours() {
        if (dateDebut != null && dateFin != null) {
            return ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        }
        return 0;
    }
}
