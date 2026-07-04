package com.example.RHBackend.repository;

import com.example.RHBackend.models.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartementRepo extends JpaRepository<Departement,Long> {
    boolean existsByNom(String nom);
}
