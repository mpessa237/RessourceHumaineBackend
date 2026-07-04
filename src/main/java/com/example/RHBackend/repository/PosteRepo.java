package com.example.RHBackend.repository;

import com.example.RHBackend.models.Poste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosteRepo extends JpaRepository<Poste,Long> {

    boolean existsByIntitule(String intitule);
}
