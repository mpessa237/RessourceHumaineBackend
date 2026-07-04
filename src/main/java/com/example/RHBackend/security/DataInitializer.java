package com.example.RHBackend.security;

import com.example.RHBackend.enums.Role;
import com.example.RHBackend.models.User;
import com.example.RHBackend.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepo userRepo ;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepo.count() == 0) {
            User admin = new User();
            admin.setUsername("herve");
            admin.setPassword(passwordEncoder.encode("herve101230"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);

            userRepo.save(admin);
            log.info("========================================");
            log.info("  Compte admin créé automatiquement");
            log.info("  Username : herve");
            log.info("  Password : herve101230");
            log.info("  !! Changez ce mot de passe en production !!");
            log.info("========================================");
        }
    }
}
