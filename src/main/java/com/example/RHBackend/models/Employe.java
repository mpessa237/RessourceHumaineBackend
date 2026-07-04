package com.example.RHBackend.models;

import com.example.RHBackend.enums.StatutEmploye;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="employes")
public class Employe {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeId;
    private String nom;
    private String prenom;
    @Column(nullable = false,unique = true)
    private String email;
    private String telephone;
    @Column(nullable = false,unique = true)
    private String matricule;
    @Column(name = "date_naissance")
    private LocalDate dateNaissance;
    @Column(name = "date_embauche", nullable = false)
    private LocalDate dateEmbauche;
    @Enumerated(EnumType.STRING)

    private StatutEmploye statutEmploye = StatutEmploye.ACTIF;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id")
    private Departement departement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poste_id")
    private Poste poste;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contrat> contrats;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Conge> conges;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Presence> presences;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
