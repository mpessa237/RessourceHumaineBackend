package com.example.RHBackend.models;

import com.example.RHBackend.enums.TypeContrat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="contrat")
public class Contrat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contratId;

    @Enumerated(EnumType.STRING)
    private TypeContrat typeContrat ;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin; // null si CDI

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal salaire;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(name = "est_actif", nullable = false)
    private Boolean estActif = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
