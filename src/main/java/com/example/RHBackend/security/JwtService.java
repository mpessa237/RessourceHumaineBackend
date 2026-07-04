package com.example.RHBackend.security;

import com.example.RHBackend.models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    @Value("${application.jwt.secret-key}")
    private String secretKey;

    @Value("${application.jwt.access-token-expiration}")
    private long accessTokenExpiration;   // ex: 900000 ms = 15 min

    @Value("${application.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;  // ex: 604800000 ms = 7 jours

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        if (user.getEmploye() != null) {
            claims.put("employeId", user.getEmploye().getEmployeId());
        }

        return buildToken(claims, user.getUsername(), accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user.getUsername(), refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Long extractEmployeId(String token) {
        return extractClaim(token, claims -> {
            Object val = claims.get("employeId");
            if (val == null) return null;
            if (val instanceof Long)    return (Long) val;
            if (val instanceof Integer) return ((Integer) val).longValue();
            return null;
        });
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isRefreshToken(String token) {
        String role = extractClaim(token,
                claims -> claims.get("role", String.class));
        return role == null; // refresh token = pas de role
    }
    public boolean isAccessToken(String token) {
        return !isRefreshToken(token);
    }


    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expiré : {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Token malformé : {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Token non supporté : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Token vide ou null : {}", e.getMessage());
        }
        return false;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
