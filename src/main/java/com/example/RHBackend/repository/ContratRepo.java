package com.example.RHBackend.repository;

import com.example.RHBackend.models.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContratRepo extends JpaRepository<Contrat,Long> {

    @Query("SELECT c FROM Contrat c " +
            "WHERE c.employe.employeId = :employeId " +
            "AND c.estActif = true")
    Optional<Contrat> findByEmployeIdAndEstActifTrue(
            @Param("employeId") Long employeId);
}
