package com.example.RHBackend.security;

import com.example.RHBackend.models.User;
import com.example.RHBackend.repository.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepo userRepo ;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader =
                request.getHeader("Authorization");
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwt = authHeader.substring(7);

        if (!jwtService.validateToken(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtService.isRefreshToken(jwt)) {
            log.warn("Refresh token detecte sur : {}",
                    request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        final String username =jwtService.extractUsername(jwt);
        if (username != null
                && SecurityContextHolder.getContext()
                .getAuthentication() == null) {
            User user = userRepo
                    .findByUsername(username)
                    .orElse(null);
            if (user != null
                    && jwtService.isTokenValid(jwt, user)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities() // ROLE_RH, ROLE_ADMIN...
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));
                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
                log.debug("User not authenticated : {} [{}]",
                        username, user.getRole());
            }
        }
        filterChain.doFilter(request, response);
    }

}
