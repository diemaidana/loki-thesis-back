package com.loki.tesis.shared.security.service;

import com.loki.tesis.auth.credential.enums.RoleType;
import com.loki.tesis.shared.security.dto.IssuedToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private Long expirationMs;

    private SecretKey secretKey;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.audience}")
    private String audience;

    public String extractUuid(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes;

        try{
            keyBytes = Decoders.BASE64.decode(secret);
        }catch(IllegalArgumentException e){
            throw new IllegalStateException("JWT secret con formato inválido. Debe ser Base64 válido.");
        }

        if(keyBytes.length < 64){
            throw new IllegalStateException("JWT secret muy corto: "+ keyBytes.length +" bytes. Se requiere mínimo 64 bytes (HS512).");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        log.info("JWT secret validado: {} bytes ({}bit)", keyBytes.length, keyBytes.length * 8);
    }

    public IssuedToken generateToken(String uuid, String email, RoleType roleType) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(getJwtExpiration());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of(roleType.name()));
        String token = buildToken(claims, uuid, email, now, expiresAt);
        return new IssuedToken(token, expiresAt);
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
                .requireAudience(audience)
                .requireIssuer(issuer)
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

    private String buildToken(Map<String, Object> extraClaims, String subject, String email, Instant now, Instant expiresAt) {

        return Jwts.builder().claims(extraClaims)
                .issuer(issuer)
                .audience().add(audience)
                .and()
                .subject(subject)
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();


    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
