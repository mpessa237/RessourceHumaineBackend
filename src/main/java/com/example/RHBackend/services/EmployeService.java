package com.example.RHBackend.services;

import com.example.RHBackend.dtos.EmployeRequest;
import com.example.RHBackend.dtos.EmployeResponse;
import com.example.RHBackend.enums.Role;
import com.example.RHBackend.enums.StatutEmploye;
import com.example.RHBackend.models.Departement;
import com.example.RHBackend.models.Employe;
import com.example.RHBackend.models.Poste;
import com.example.RHBackend.models.User;
import com.example.RHBackend.repository.DepartementRepo;
import com.example.RHBackend.repository.EmployeRepo;
import com.example.RHBackend.repository.PosteRepo;
import com.example.RHBackend.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final EmployeRepo employeRepo;
    private final DepartementRepo departementRepo;
    private final PosteRepo posteRepo;

    @Transactional
    public EmployeResponse creer(EmployeRequest employeRequest) {

        if (userRepo.existsByUsername(employeRequest.getEmail())) {
            throw new RuntimeException(
                    "Un compte existe deja avec cet email : "
                            + employeRequest.getEmail());
        }
        // 2. Generer le mot de passe initial
        // Si non fourni -> genere un mot de passe aleatoire
        String motDePasse = (employeRequest.getPassword() != null
                && !employeRequest.getPassword().isBlank())
                ? employeRequest.getPassword()
                : genererMotDePasse();

        User user = new User();
        user.setUsername(employeRequest.getEmail());
        user.setPassword(passwordEncoder.encode(motDePasse));
        user.setRole(Role.EMPLOYE);
        user.setEnabled(true);
        userRepo.save(user);

// 4. Resoudre departement et poste si fournis
        Departement departement = null;
        if (employeRequest.getDepartementId() != null) {
            departement = departementRepo.findById(employeRequest.getDepartementId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Departement introuvable : "
                                    + employeRequest.getDepartementId()));
        }
        Poste poste = null;
        if (employeRequest.getPosteId() != null) {
            poste = posteRepo.findById(employeRequest.getPosteId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Poste introuvable : "
                                    + employeRequest.getPosteId()));
        }

        // 5. Creer le profil Employe lie au User
        Employe employe = new Employe();
        employe.setMatricule(genererMatricule());
        employe.setNom(employeRequest.getNom());
        employe.setPrenom(employeRequest.getPrenom());
        employe.setEmail(employeRequest.getEmail());
        employe.setTelephone(employeRequest.getTelephone());
        employe.setDateEmbauche(employeRequest.getDateEmbauche());
        employe.setStatutEmploye(StatutEmploye.ACTIF);
        employe.setDepartement(departement);
        employe.setPoste(poste);
        employe.setUser(user);
        employeRepo.save(employe);

        // 6. TODO: envoyer les credentials par email
        return toResponse(employe);
    }

    private EmployeResponse toResponse(Employe employe) {

        EmployeResponse employeResponse = new EmployeResponse();
        employeResponse.setEmployeId(String.valueOf(employe.getEmployeId()));
        employeResponse.setMatricule(employe.getMatricule());
        employeResponse.setNom(employe.getNom());
        employeResponse.setPrenom(employe.getPrenom());
        employeResponse.setEmail(employe.getEmail());
        employeResponse.setTelephone(employe.getTelephone());
        employeResponse.setDateEmbauche(String.valueOf(employe.getDateEmbauche()));
        employeResponse.setStatutEmploye(employe.getStatutEmploye());
        employeResponse.setDepartementNom(employe.getDepartement() != null
                ? employe.getDepartement().getNom() : null);
        employeResponse.setPosteIntitule(employe.getPoste() != null
                ? employe.getPoste().getIntitule() : null);
        employeResponse.setUserId(employe.getUser() != null
                ? employe.getUser().getUserId() : null);
        employeResponse.setCreatedAt(employe.getCreatedAt());

        return employeResponse;

    }


    private String genererMatricule() {
        int annee = LocalDate.now().getYear();
        String suffix = UUID.randomUUID()
                .toString().substring(0, 4).toUpperCase();
        return "EMP-" + annee + "-" + suffix;
    }

    private String genererMotDePasse() {
        return UUID.randomUUID()
                .toString().replace("-", "").substring(0, 10);
    }

    public List<EmployeResponse> listerAll() {
        return employeRepo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EmployeResponse voirParId(Long employeId) {
        Employe employe = employeRepo.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Employe introuvable : " + employeId));
        return toResponse(employe);
    }

    // ■■ RECHERCHER ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    // Recherche par nom, prenom ou matricule (insensible a la casse)
    public List<EmployeResponse> rechercher(String query) {
        if (query == null || query.isBlank()) {
            return listerAll();
        }
        return employeRepo
                .searchByNomOrPrenomOrMatricule(query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmployeResponse changerStatut(
            Long employeId, StatutEmploye nouveauStatut) {
        Employe employe = employeRepo.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Employe introuvable : " + employeId));
        employe.setStatutEmploye(nouveauStatut);
        // Desactiver aussi le compte User si INACTIF
        boolean actif = nouveauStatut == StatutEmploye.ACTIF;
        employe.getUser().setEnabled(actif);
        return toResponse(employeRepo.save(employe));
    }

    @Transactional
    public EmployeResponse modifier(Long employeId, EmployeRequest employeRequest) {
        Employe employe = employeRepo.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Employe introuvable : " + employeId));
        // Verifier email unique si change
        if (!employe.getEmail().equals(employeRequest.getEmail())
                && userRepo.existsByUsername(employeRequest.getEmail())) {
            throw new RuntimeException(
                    "Cet email est deja utilise.");
        }
        employe.setNom(employeRequest.getNom());
        employe.setPrenom(employeRequest.getPrenom());
        employe.setEmail(employeRequest.getEmail());
        employe.setTelephone(employeRequest.getTelephone());
        employe.setDateEmbauche(employeRequest.getDateEmbauche());
        if (employeRequest.getDepartementId() != null) {
            Departement dept = departementRepo
                    .findById(employeRequest.getDepartementId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Departement introuvable"));
            employe.setDepartement(dept);
        }
        if (employeRequest.getPosteId() != null) {
            Poste poste = posteRepo
                    .findById(employeRequest.getPosteId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Poste introuvable"));
            employe.setPoste(poste);
        }
        employe.getUser().setUsername(employeRequest.getEmail());
        return toResponse(employeRepo.save(employe));

    }
}
