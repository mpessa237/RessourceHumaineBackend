package com.example.RHBackend.services;

import com.example.RHBackend.dtos.CongeRequest;
import com.example.RHBackend.dtos.CongeResponse;
import com.example.RHBackend.dtos.RejetRequest;
import com.example.RHBackend.enums.StatutConge;
import com.example.RHBackend.models.Conge;
import com.example.RHBackend.models.Employe;
import com.example.RHBackend.models.SoldeConge;
import com.example.RHBackend.models.User;
import com.example.RHBackend.repository.CongeRepo;
import com.example.RHBackend.repository.EmployeRepo;
import com.example.RHBackend.repository.SoldeCongeRepo;
import com.example.RHBackend.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CongeService {

    private final EmployeRepo employeRepo;
    private final SoldeCongeRepo soldeCongeRepo;
    private final UserRepo userRepo;
    private final CongeRepo congeRepo;

    @Transactional
    public CongeResponse soumettre(CongeRequest congeRequest, String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Utilisateur introuvable"));
        Employe employe = user.getEmploye();
        if (employe == null) {
            throw new RuntimeException(
                    "Aucun profil employe lie a ce compte.");
        }

        if (congeRequest.getDateFin().isBefore(congeRequest.getDateDebut())) {
            throw new RuntimeException(
                    "La date de fin ne peut pas etre "
                            + "avant la date de debut.");
        }

        Conge conge = new Conge();
        conge.setTypeConge(congeRequest.getTypeConge());
        conge.setDateDebut(congeRequest.getDateDebut());
        conge.setDateFin(congeRequest.getDateFin());
        conge.setMotif(congeRequest.getMotif());
        conge.setStatutConge(StatutConge.EN_ATTENTE);
        conge.setEmploye(employe);

        return toResponse(congeRepo.save(conge));
    }

    public List<CongeResponse> listerTous() {
        return congeRepo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    public CongeResponse voirParId(Long congeId) {
        return toResponse(congeRepo.findById(congeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Demande de conge introuvable : " + congeId)));
    }

    // ── APPROUVER ────────────────────────────────────────────────
    @Transactional
    public CongeResponse approuver(Long congeId, String username) {

        // 1. Charger la demande
        Conge conge = congeRepo.findById(congeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Demande introuvable : " + congeId));

        // 2. Vérifier que la demande est bien EN_ATTENTE
        if (conge.getStatutConge() != StatutConge.EN_ATTENTE) {
            throw new RuntimeException(
                    "Seules les demandes EN_ATTENTE peuvent être approuvées.");
        }

        // 3. Calculer le nombre de jours
        long nombreJours = ChronoUnit.DAYS.between(
                conge.getDateDebut(), conge.getDateFin()) + 1;

        // 4. Vérifier et déduire le solde
        int annee = conge.getDateDebut().getYear();
        SoldeConge soldeConge = soldeCongeRepo
                .findByEmployeIdAndAnnee(conge.getEmploye().getEmployeId(), annee)
                .orElseGet(() -> creerSoldeInitial(conge.getEmploye(), annee));

        if (nombreJours > soldeConge.getJoursRestants()) {
            throw new RuntimeException(
                    "Solde insuffisant. Restant : "
                            + soldeConge.getJoursRestants()
                            + " jour(s), demande : "
                            + nombreJours + " jour(s).");
        }

        soldeConge.setJoursUtilises(soldeConge.getJoursUtilises() + (int) nombreJours);
        soldeCongeRepo.save(soldeConge);

        // 5. Mettre à jour le statut et le validateur
        conge.setStatutConge(StatutConge.APPROUVE);
        conge.setDateDecision(LocalDateTime.now());

        User validateur = userRepo.findByUsername(username).orElseThrow();
        conge.setValidateur(validateur);

        return toResponse(congeRepo.save(conge));
    }

    // ── REJETER ──────────────────────────────────────────────────
    @Transactional
    public CongeResponse rejeter(Long congeId, RejetRequest rejetRequest , String username) {

        // 1. Charger la demande
        Conge conge = congeRepo.findById(congeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Demande introuvable : " + congeId));

        // 2. Vérifier que la demande est bien EN_ATTENTE
        if (conge.getStatutConge() != StatutConge.EN_ATTENTE) {
            throw new RuntimeException(
                    "Seules les demandes EN_ATTENTE peuvent être rejetées.");
        }

        // 3. Mettre à jour — pas de déduction du solde en cas de rejet
        conge.setStatutConge(StatutConge.REJETE);
        conge.setCommentaireRh(rejetRequest.getCommentaire());
        conge.setDateDecision(LocalDateTime.now());

        User validateur = userRepo.findByUsername(username).orElseThrow();
        conge.setValidateur(validateur);

        return toResponse(congeRepo.save(conge));
    }

    // ── UTILITAIRE — crée un solde initial de 30 jours ──────────
    private SoldeConge creerSoldeInitial(Employe employe, int annee) {
        SoldeConge solde = new SoldeConge();
        solde.setEmploye(employe);
        solde.setAnnee(annee);
        solde.setJoursAccordes(30);
        solde.setJoursUtilises(0);
        return soldeCongeRepo.save(solde);
    }


    private CongeResponse toResponse(Conge conge) {
        long nbJours = ChronoUnit.DAYS.between(
                conge.getDateDebut(), conge.getDateFin()) + 1;

        CongeResponse congeResponse = new CongeResponse();
        congeResponse.setCongeId(conge.getCongeId());
        congeResponse.setEmployeNom(conge.getEmploye().getNom());
        congeResponse.setEmployePrenom(conge.getEmploye().getPrenom());
        congeResponse.setEmployeMatricule(conge.getEmploye().getMatricule());
        congeResponse.setTypeConge(conge.getTypeConge());
        congeResponse.setDateDebut(conge.getDateDebut());
        congeResponse.setDateFin(conge.getDateFin());
        congeResponse.setNombreJours(nbJours);
        congeResponse.setMotif(conge.getMotif());
        congeResponse.setStatutConge(conge.getStatutConge());
        congeResponse.setCreatedAt(conge.getCreatedAt());

        return congeResponse;
    }

}
