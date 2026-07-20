package com.example.RHBackend.services;

import com.example.RHBackend.dtos.ContratRequest;
import com.example.RHBackend.dtos.ContratResponse;
import com.example.RHBackend.enums.TypeContrat;
import com.example.RHBackend.models.Contrat;
import com.example.RHBackend.models.Employe;
import com.example.RHBackend.repository.ContratRepo;
import com.example.RHBackend.repository.EmployeRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContratService {

    private final ContratRepo contratRepo;
    private final EmployeRepo employeRepo;

    @Transactional
    public ContratResponse creer(ContratRequest contratRequest) {

        // 1. Charger l'employé
        Employe employe = employeRepo.findById(contratRequest.getEmployeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Employe introuvable : " + contratRequest.getEmployeId()));

        // 2. Vérifier cohérence des dates selon le type
        if (contratRequest.getTypeContrat() != TypeContrat.CDI) {
            if (contratRequest.getDateFin() == null) {
                throw new RuntimeException(
                        "La date de fin est obligatoire pour un "
                                + contratRequest.getTypeContrat());
            }
            if (contratRequest.getDateFin().isBefore(contratRequest.getDateDebut())) {
                throw new RuntimeException(
                        "La date de fin ne peut pas être avant la date de début.");
            }
        }

        // 3. Désactiver l'ancien contrat actif s'il existe
        contratRepo.findByEmployeIdAndEstActifTrue(employe.getEmployeId())
                .ifPresent(ancien -> {
                    ancien.setEstActif(false);
                    contratRepo.save(ancien);
                });

        // 4. Créer le nouveau contrat
        Contrat contrat = new Contrat();
        contrat.setEmploye(employe);
        contrat.setTypeContrat(contratRequest.getTypeContrat());
        contrat.setDateDebut(contratRequest.getDateDebut());
        contrat.setDateFin(contratRequest.getDateFin());  // null si CDI
        contrat.setSalaire(contratRequest.getSalaire());
        contrat.setObservations(contratRequest.getObservations());
        contrat.setEstActif(true);

        return toResponse(contratRepo.save(contrat));
    }


    public List<ContratResponse> listerTous(){
        return contratRepo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ContratResponse voirParId(Long contratId){
        return toResponse(contratRepo.findById(contratId)
                .orElseThrow(()-> new EntityNotFoundException(
                        "contrat introuvable : " + contratId
                )));
    }

    private ContratResponse toResponse(Contrat contrat) {

        ContratResponse contratResponse = new ContratResponse();
        contratResponse.setContratId(contrat.getContratId());
        contratResponse.setEmployeNom(contrat.getEmploye().getNom());
        contratResponse.setEmployePrenom(contrat.getEmploye().getPrenom());
        contratResponse.setEmployeMatricule(contrat.getEmploye().getMatricule());
        contratResponse.setTypeContrat(contrat.getTypeContrat());
        contratResponse.setDateDebut(contrat.getDateDebut());
        contratResponse.setDateFin(contrat.getDateFin());
        contratResponse.setSalaire(contrat.getSalaire());
        contratResponse.setObservations(contrat.getObservations());
        contratResponse.setEstActif(contrat.getEstActif());
        contratResponse.setCreatedAt(contrat.getCreatedAt());

        return contratResponse;
    }
}
