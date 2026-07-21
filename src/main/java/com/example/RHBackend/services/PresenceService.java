package com.example.RHBackend.services;

import com.example.RHBackend.dtos.PresenceRequest;
import com.example.RHBackend.dtos.PresenceResponse;
import com.example.RHBackend.enums.StatutPresence;
import com.example.RHBackend.models.Employe;
import com.example.RHBackend.models.Presence;
import com.example.RHBackend.repository.CongeRepo;
import com.example.RHBackend.repository.EmployeRepo;
import com.example.RHBackend.repository.PresenceRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final PresenceRepo presenceRepo;
    private final EmployeRepo employeRepo;
    private final CongeRepo congeRepo;

    // ── POINTER UNE PRESENCE ─────────────────────────────────
    @Transactional
    public PresenceResponse pointer(PresenceRequest presenceRequest) {

        // 1. Charger l'employé
        Employe employe = employeRepo.findById(presenceRequest.getEmployeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Employé introuvable : " + presenceRequest.getEmployeId()));

        // 2. Vérifier qu'une présence n'existe pas déjà ce jour
        if (presenceRepo.existsByEmployeIdAndDate(
                employe.getEmployeId(), presenceRequest.getDate())) {
            throw new RuntimeException(
                    "Une présence existe déjà pour cet employé "
                            + "à la date : " + presenceRequest.getDate());
        }

        // 3. Vérifier si l'employé est en congé ce jour
        // Si oui → statut automatiquement CONGE
        boolean enConge = congeRepo.existsCongeEnChevauchement(
                employe.getEmployeId(),
                presenceRequest.getDate(),
                presenceRequest.getDate());

        StatutPresence statutPresence = enConge
                ? StatutPresence.CONGE
                : presenceRequest.getStatutPresence();

        // 4. Créer la présence
        Presence presence = new Presence();
        presence.setEmploye(employe);
        presence.setDate(presenceRequest.getDate());
        presence.setHeureArrivee(presenceRequest.getHeureArrivee());
        presence.setHeureDepart(presenceRequest.getHeureDepart());
        presence.setStatutPresence(statutPresence);
        presence.setObservations(presenceRequest.getObservations());

        return toResponse(presenceRepo.save(presence));
    }

    // ── LISTER TOUTES ────────────────────────────────────────
    public List<PresenceResponse> listerToutes() {
        return presenceRepo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<PresenceResponse> voirParEmploye(Long employeId) {
        employeRepo.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Employé introuvable : " + employeId));

        return presenceRepo.findByEmployeId(employeId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── VOIR PAR DATE ────────────────────────────────────────
    public List<PresenceResponse> voirParDate(LocalDate date) {
        return presenceRepo.findByDate(date)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── MODIFIER ─────────────────────────────────────────────
    @Transactional
    public PresenceResponse modifier(Long id, PresenceRequest request) {

        Presence presence = presenceRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Présence introuvable : " + id));

        // Vérifier doublon si la date change
        if (!presence.getDate().equals(request.getDate()) &&
                presenceRepo.existsByEmployeIdAndDate(
                        presence.getEmploye().getEmployeId(), request.getDate())) {
            throw new RuntimeException(
                    "Une présence existe déjà pour cette date.");
        }

        presence.setDate(request.getDate());
        presence.setHeureArrivee(request.getHeureArrivee());
        presence.setHeureDepart(request.getHeureDepart());
        presence.setStatutPresence(request.getStatutPresence());
        presence.setObservations(request.getObservations());

        return toResponse(presenceRepo.save(presence));
    }

    // ── MAPPING PRIVÉ ─────────────────────────────────────────
    private PresenceResponse toResponse(Presence presence) {

        PresenceResponse presenceResponse = new PresenceResponse();
        presenceResponse.setId(presence.getId());
        presenceResponse.setEmployeId(presence.getEmploye().getEmployeId());
        presenceResponse.setEmployeNom(presence.getEmploye().getNom());
        presenceResponse.setEmployePrenom(presence.getEmploye().getPrenom());
        presenceResponse.setEmployeMatricule(presence.getEmploye().getMatricule());
        presenceResponse.setDate(presence.getDate());
        presenceResponse.setHeureArrivee(presence.getHeureArrivee());
        presenceResponse.setHeureDepart(presence.getHeureDepart());
        presenceResponse.setDureeHeures(presence.getDureeHeures());
        presenceResponse.setStatutPresence(presence.getStatutPresence());
        presenceResponse.setObservations(presence.getObservations());
        presenceResponse.setCreatedAt(presence.getCreatedAt());

        return presenceResponse;
    }
}
