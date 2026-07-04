package com.example.RHBackend.models;

import com.example.RHBackend.enums.StatutPresence;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "presences",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employe_id", "date"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Presence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "heure_arrivee")
    private LocalTime heureArrivee;

    @Column(name = "heure_depart")
    private LocalTime heureDepart;

    @Enumerated(EnumType.STRING)
    private StatutPresence statutPresence = StatutPresence.PRESENT;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Calcul de la durée de travail en heures
    public double getDureeHeures() {
        if (heureArrivee != null && heureDepart != null) {
            Duration duration = Duration.between(heureArrivee, heureDepart);
            return duration.toMinutes() / 60.0;
        }
        return 0.0;
    }
}
