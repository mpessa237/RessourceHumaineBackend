package com.example.RHBackend.repository;

import com.example.RHBackend.models.SoldeConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SoldeCongeRepo extends JpaRepository<SoldeConge,Long> {


    @Query("SELECT s FROM SoldeConge s " +
            "WHERE s.employe.employeId = :employeId " +
            "AND s.annee = :annee")
    Optional<SoldeConge> findByEmployeIdAndAnnee(
            @Param("employeId") Long employeId,
            @Param("annee") int annee);
}
