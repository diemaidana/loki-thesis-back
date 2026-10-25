package com.loki.tesis.shared.security.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/*
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override public void commence(@NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull AuthenticationException authException)
            throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String errorMessage = switch (authException) {
            case BadCredentialsException badCredentialsException -> "Credenciales inválidas";
            case DisabledException disabledException -> "Cuenta deshabilitada";
            case LockedException lockedException -> "Cuenta bloqueada";
            case AccountExpiredException accountExpiredException -> "Cuenta expirada";
            case CredentialsExpiredException credentialsExpiredException -> "Credenciales expiradas";
            case InsufficientAuthenticationException insufficientAuthenticationException -> "Autenticación insuficiente";
            case AuthenticationServiceException authenticationServiceException -> "Error en el servicio de autenticación";
            default -> "Error de autenticación: " + authException.getMessage(); };

    }
}
*/

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint{
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver resolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException){
        resolver.resolveException(request, response, null, authException);
    }
}