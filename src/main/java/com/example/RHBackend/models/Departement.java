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
@Table(name="departements")
public class Departement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departementId;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "departement", fetch = FetchType.LAZY)
    private List<Employe> employes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
