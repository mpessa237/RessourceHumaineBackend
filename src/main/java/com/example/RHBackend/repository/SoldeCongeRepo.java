package com.example.RHBackend.repository;

import com.example.RHBackend.models.SoldeConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoldeCongeRepo extends JpaRepository<SoldeConge,Long> {

    Optional<SoldeConge> findByEmployeIdAndAnnee(Long employeId, int annee);

    boolean existsByEmployeIdAndAnnee(Long employeId, int annee);

}
