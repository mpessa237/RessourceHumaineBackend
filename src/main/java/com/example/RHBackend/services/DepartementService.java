package com.example.RHBackend.services;

import com.example.RHBackend.dtos.DepartementRequest;
import com.example.RHBackend.dtos.DepartementResponse;
import com.example.RHBackend.models.Departement;
import com.example.RHBackend.repository.DepartementRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartementService {

    private final DepartementRepo departementRepo;

    @Transactional
    public DepartementResponse creer(DepartementRequest departementRequest) {
        // Verifier que le nom est unique
        if (departementRepo.existsByNom(departementRequest.getNom())) {
            throw new RuntimeException(
                    "Un departement avec ce nom existe deja : "
                            + departementRequest.getNom());
        }
        Departement dept = new Departement();
        dept.setNom(departementRequest.getNom());
        dept.setDescription(departementRequest.getDescription());
        return toResponse(departementRepo.save(dept));
    }

    private DepartementResponse toResponse(Departement departement) {
        int effectif = (departement.getEmployes() != null)
                ? departement.getEmployes().size() : 0;

        DepartementResponse departementResponse = new DepartementResponse();
        departementResponse.setDepartementId(departement.getDepartementId());
        departementResponse.setNom(departement.getNom());
        departementResponse.setDescription(departement.getDescription());
        departementResponse.setNombreEmployes(effectif);
        departementResponse.setCreatedAt(departement.getCreatedAt());

        return departementResponse;
    }

    public List<DepartementResponse> litserTous() {
        return departementRepo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DepartementResponse voirParId(Long departementId) {
        return toResponse(departementRepo.findById(departementId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Departement introuvable : " + departementId)));
    }

    @Transactional
    public DepartementResponse modifier(
            Long departementId, DepartementRequest departementRequest) {
        Departement departement = departementRepo.findById(departementId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Departement introuvable : " + departementId));
        // Verifier unicite du nom si change
        if (!departement.getNom().equals(departementRequest.getNom())
                && departementRepo.existsByNom(departementRequest.getNom())) {
            throw new RuntimeException(
                    "Ce nom est deja utilise.");
        }
        departement.setNom(departement.getNom());
        departement.setDescription(departementRequest.getDescription());
        return toResponse(departementRepo.save(departement));
    }


    @Transactional
    public void supprimer(Long departementId) {
        Departement dept = departementRepo.findById(departementId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Departement introuvable : " + departementId));

        if (dept.getEmployes() != null
                && !dept.getEmployes().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer ce departement : "
                            + dept.getEmployes().size()
                            + " employe(s) y sont rattaches.");
        }
        departementRepo.delete(dept);
    }

}
