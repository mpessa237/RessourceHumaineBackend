package com.example.RHBackend.services;

import com.example.RHBackend.dtos.AuthResponse;
import com.example.RHBackend.dtos.LoginRequest;
import com.example.RHBackend.dtos.RefreshTokenRequest;
import com.example.RHBackend.models.User;
import com.example.RHBackend.repository.UserRepo;
import com.example.RHBackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepo userRepo;

    public @Nullable AuthResponse authenticate(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepo.findByUsername(loginRequest.getUsername())
                .orElseThrow(()->new IllegalArgumentException("user not found!!"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setRole(user.getRole());
        authResponse.setEmployeId(
                user .getEmploye() !=null
                ? user.getEmploye().getEmployeId()
                        :null
        );
        return authResponse;
    }


    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (!jwtService.validateToken(refreshToken)){
            throw new RuntimeException("Refresh token invalide ou expire" +
                    "veuillez vous reconnecter");
        }

        if (!jwtService.isRefreshToken(refreshToken)){
            throw new RuntimeException(
                    "Token fourni n'est pas un refresh token"
            );
        }

        String username = jwtService.extractUsername(refreshToken);

        User user = userRepo.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("user not found"));

        if (!user.isEnabled()){
            throw new RuntimeException("compte desactiver" + "contactez l'admin");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(newAccessToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setRole(user.getRole());
        authResponse.setEmployeId(user.getEmploye() !=null ?
                user.getEmploye().getEmployeId() : null);

        return authResponse;
    }
}
