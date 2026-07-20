package com.example.RHBackend.repository;

import com.example.RHBackend.models.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PresenceRepo extends JpaRepository<Presence,Long> {

    @Query("SELECT p FROM Presence p WHERE p.employe.employeId = :employeId")
    List<Presence> findByEmployeId(@Param("employeId") Long employeId);

    @Query("SELECT p FROM Presence p WHERE p.date = :date")
    List<Presence> findByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(p) > 0 FROM Presence p " +
            "WHERE p.employe.employeId = :employeId " +
            "AND p.date = :date")
    boolean existsByEmployeIdAndDate(
            @Param("employeId") Long employeId,
            @Param("date") LocalDate date);

    @Query("SELECT p FROM Presence p " +
            "WHERE p.employe.employeId = :employeId " +
            "AND p.date = :date")
    Optional<Presence> findByEmployeIdAndDate(
            @Param("employeId") Long employeId,
            @Param("date") LocalDate date);
}
