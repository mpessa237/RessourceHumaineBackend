package com.example.RHBackend.repository;

import com.example.RHBackend.models.Conge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface CongeRepo extends JpaRepository<Conge,Long> {

    // Vérifie chevauchement de dates — utilisée dans approuver()
    // pour s'assurer qu'on n'approuve pas deux congés en même temps
    @Query("SELECT COUNT(c) > 0 FROM Conge c " +
            "WHERE c.employe.id = :employeId " +
            "AND c.statutConge NOT IN ('REJETE', 'ANNULE') " +
            "AND c.dateDebut <= :dateFin " +
            "AND c.dateFin >= :dateDebut")
    boolean existsCongeEnChevauchement(
            @Param("employeId") Long employeId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

}
