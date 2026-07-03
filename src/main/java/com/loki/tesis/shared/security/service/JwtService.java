package com.loki.tesis.shared.security.service;

import com.loki.tesis.auth.credential.enums.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private Long expirationMs;

    private SecretKey secretKey;

    public String extractUuid(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(String uuid, String email, RoleType roleType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of(roleType.name()));
        return buildToken(claims, uuid, email, expirationMs);
    }

    public Duration getJwtExpiration() {
        return Duration.ofMillis(expirationMs);
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        List<?> rawRoles = claims.get("roles", List.class);
        if (rawRoles == null || rawRoles.isEmpty()) {
            return Collections.emptyList();
        }
        return rawRoles.stream()
                .map(Object::toString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private <T> Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((this.secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    /*
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getEmail()))
                && !isTokenExpired(token)
                && userDetails.isAccountNonLocked()
                && userDetails.isEnabled();
    }
    */

    private String buildToken(Map<String, Object> extraClaims, String subject, String email, long expirationMs) {
        return Jwts.builder().claims(extraClaims)
                .subject(subject)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
