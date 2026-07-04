package com.example.RHBackend.services;

import com.example.RHBackend.dtos.PosteRequest;
import com.example.RHBackend.dtos.PosteResponse;
import com.example.RHBackend.models.Poste;
import com.example.RHBackend.repository.PosteRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PosteService {

    private final PosteRepo posteRepo;

    @Transactional
    public PosteResponse creer(PosteRequest posteRequest) {
        if (posteRepo.existsByIntitule(posteRequest.getIntitule())) {
            throw new RuntimeException(
                    "Un poste avec cet intitule existe deja : "
                            + posteRequest.getIntitule());
        }
        Poste poste = new Poste();
        poste.setIntitule(posteRequest.getIntitule());
        poste.setNiveauRequis(posteRequest.getNiveauRequis());
        poste.setDescription(posteRequest.getDescription());
        return toResponse(posteRepo.save(poste));
    }

    private PosteResponse toResponse(Poste poste) {

        int effectif = (poste.getEmployes() != null)
                ? poste.getEmployes().size() : 0;
        PosteResponse posteResponse = new PosteResponse();
        posteResponse.setPosteId(poste.getPosteId());
        posteResponse.setIntitule(poste.getIntitule());
        posteResponse.setNiveauRequis(poste.getNiveauRequis());
        posteResponse.setDescription(poste.getDescription());
        posteResponse.setNombreEmployes(effectif);
        posteResponse.setCreatedAt(poste.getCreatedAt());

        return posteResponse;
    }


    public List<PosteResponse> listerTous() {
        return posteRepo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PosteResponse voirParId(Long posteId) {
        return toResponse(posteRepo.findById(posteId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Poste introuvable : " + posteId)));
    }

    @Transactional
    public PosteResponse modifier(Long posteId, PosteRequest posteRequest) {
        Poste poste = posteRepo.findById(posteId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Poste introuvable : " + posteId));
        if (!poste.getIntitule().equals(posteRequest.getIntitule())
                && posteRepo.existsByIntitule(posteRequest.getIntitule())) {
            throw new RuntimeException(
                    "Cet intitule est deja utilise.");
        }
        poste.setIntitule(posteRequest.getIntitule());
        poste.setNiveauRequis(posteRequest.getNiveauRequis());
        poste.setDescription(posteRequest.getDescription());
        return toResponse(posteRepo.save(poste));
    }

    @Transactional
    public void supprimer(Long posteId) {
        Poste poste = posteRepo.findById(posteId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Poste introuvable : " + posteId));
        if (poste.getEmployes() != null
                && !poste.getEmployes().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer ce poste : "
                            + poste.getEmployes().size()
                            + " employe(s) occupent ce poste.");
        }
        posteRepo.delete(poste);
    }
}
