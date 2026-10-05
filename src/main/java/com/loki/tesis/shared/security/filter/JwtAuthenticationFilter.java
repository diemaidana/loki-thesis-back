package com.loki.tesis.shared.security.filter;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.shared.security.service.CredentialUserDetailsService;
import com.loki.tesis.shared.security.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CredentialUserDetailsService credentialUserDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorizationHeader.substring(7);
        try{

            final UUID uuid = UUID.fromString(jwtService.extractUuid(token));
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null) {
                Credential credential = credentialUserDetailsService.loadUserByUuid(uuid);
                List<GrantedAuthority> authorities = jwtService.getAuthorities(token);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        credential,
                        null,
                        authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
        catch(JwtException | IllegalArgumentException | UsernameNotFoundException jwtException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(String.format("{\"error\": \"Token JWT invalido o expirado\", \"status\": %d, \"path\": \"%s\"}",
            HttpServletResponse.SC_UNAUTHORIZED, request.getRequestURI()));
            response.getWriter().flush();
            return;
        }
        filterChain.doFilter(request, response);
    }
}
