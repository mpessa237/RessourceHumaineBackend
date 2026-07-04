package com.example.RHBackend.repository;

import com.example.RHBackend.models.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeRepo extends JpaRepository<Employe,Long> {

    boolean existsByEmail(String email);

    @Query("SELECT e FROM Employe e WHERE " +
                 "LOWER(e.nom) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
                 "LOWER(e.prenom) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
                 "LOWER(e.matricule) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Employe> searchByNomOrPrenomOrMatricule(
            @Param("q") String query);
}
