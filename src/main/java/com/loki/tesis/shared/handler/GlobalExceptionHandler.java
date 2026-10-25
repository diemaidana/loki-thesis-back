package com.loki.tesis.shared.handler;

import com.loki.tesis.auth.exception.*;
import com.loki.tesis.auth.verification.verificationToken.exception.InvalidTokenTypeException;
import com.loki.tesis.auth.verification.verificationToken.exception.TokenAlreadyUsedException;
import com.loki.tesis.auth.verification.verificationToken.exception.TokenExpiredException;
import com.loki.tesis.auth.verification.verificationToken.exception.TokenNotFoundException;
import com.loki.tesis.shared.email.exception.EmailSendException;
import com.loki.tesis.shared.exception.dto.GlobalError;
import com.loki.tesis.shared.exception.dto.ValidationError;
import com.loki.tesis.user.exception.UserAlreadyInactiveException;
import com.loki.tesis.user.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Campos que no deben aparecer
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password",
            "currentpassword",
            "newpassword",
            "token",
            "secret"
    );

    // 404 - Not Found
    // en caso de que Credentials no encuentre el email o la password en la base de datos.
    @ExceptionHandler(CredentialNotFoundException.class)
    public ProblemDetail handleCredentialNotFound(CredentialNotFoundException ex) {
        log.debug("Credential no encontrada: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Credenciales no encontradas.");
    }

    // 404 - Not Found
    // Se dispara cuando no se encuentra el usuario.
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        log.debug("Usuario no encontrado: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Usuario no encontrado.");
    }


    // 202 - Conflict
    // Se dispara cuando el email ya se encuentra registrado.
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.debug("Email ya registrado: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.ACCEPTED, "Si el email no estaba registrado, te enviamos un email de verificación.");
    }

    // 409 - Conflict
    @ExceptionHandler(UserAlreadyInactiveException.class)
    public ProblemDetail handleUserAlreadyInactive(UserAlreadyInactiveException ex) {
        log.debug("Usuario ya esta inactivo: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "La cuenta ya está inactiva.");
    }

    // 400 - BadRequest
    // Se dispara cuando un @RequestBody anotado con @Valid NO pasa las validaciones
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex){

        // BindingResult contiene todos los errores detectados en la validacion
        BindingResult result = ex.getBindingResult();

        /* No me gusto la funcion de abajo y refactoreo ya que la uso en otros dos handlers (puede haber mas) */
        List<ValidationError> fieldErrors = result.getFieldErrors()
                .stream()
                .map(this::toValidationError)
                .toList();

        // Errores globales (validaciones a nivel de objeto, ej: @AssertTrue)
        // No me gusta y me gustaria refactorearlo pero nada mas usa este globalErrors. (Se podria hacer lo mismo que en FieldErrors.
        List<GlobalError> globalErrors = result.getGlobalErrors().stream()
                .map(ge -> new GlobalError(
                        ge.getCode(),       // tipo de error
                        ge.getDefaultMessage() != null ? ge.getDefaultMessage() : "Error de validacion" // Respuesta por si es NULL.
                ))
                .toList();

        log.debug("Validacion fallida: {} errores de campo, {} globales",
                fieldErrors.size(), globalErrors.size());

        return buildValidationProblem(fieldErrors, globalErrors, "La solicitud contiene errores.");
    }

    // 400 - Bad Request
    // Se dispara cuando el cuerpo de la request no se puede parsear
    // JSON cortado, vacio, etc.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex){
        log.debug("Body no parseable: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud esta mal o vacio."
        );
    }

    // 400 - Bad Request
    // Salta cuando un @PathVariable o @RequestParam con validacion falla.
    // Es necesario poner @Validated en controller que es quien recibe el @RequestParam o el @PathVariable.
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {

        List<ValidationError> errors = ex.getConstraintViolations()
                .stream()
                .map(this::toValidationError)
                .toList();

        log.debug("Error de restricciones en parametros: {} errores", errors.size());

        return buildValidationProblem(errors, List.of(), "Errores de validacion en parametros");
    }
    // 500 - Internal Server Error
    @ExceptionHandler(EmailSendException.class)
    public ProblemDetail handleEmailSend(EmailSendException ex) {
        log.error("Email send no encontrado: {}", ex.getMessage(), ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo enviar el email. Intentá más tarde.");
    }

    // 404 - El token no existe.
    @ExceptionHandler(TokenNotFoundException.class)
    public ProblemDetail handleTokenNotFound(TokenNotFoundException ex) {
        log.debug("Token no encontrado: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 401 - Contraseña o email incorrectos.
    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        log.debug("Credential no encontrado: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos.");
    }

    //401
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLockingFailure(OptimisticLockingFailureException ex) {
        log.warn("Optimistic locking failure no encontrado: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos.");
    }


    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(ForbiddenException ex) {
        log.debug("Forbidden no encontrado: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // 403 - El email no fue verificado.
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ProblemDetail handleEmailNotVerified(EmailNotVerifiedException ex) {
        log.debug("Email no encontrado: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "El email no fue verificado.");
    }

    // 400 - Tipo de token invalido.
    @ExceptionHandler(InvalidTokenTypeException.class)
    public ProblemDetail handleInvalidTokenType(InvalidTokenTypeException ex) {
        log.debug("Tipo de token invalido: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    // 423 - Cuenta bloqueada por repetidos intentos de login fallidos
    @ExceptionHandler(AccountLockedException.class)
    public ProblemDetail handleAccountLocked(AccountLockedException ex) {
        log.debug("Usuario bloqueado: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.LOCKED, ex.getMessage());
    }
    // 410 - Gone - El token existió pero expiró
    @ExceptionHandler(TokenExpiredException.class)
    public ProblemDetail handleTokenExpired(TokenExpiredException ex) {
        log.debug("Token ya expiró: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.GONE, ex.getMessage());
    }

    // 409 - Conflict - Debido a que el token ya se utilizó.
    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ProblemDetail handleTokenAlreadyUsed(TokenAlreadyUsedException ex) {
        log.debug("Token ya se utilizó: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 500 - Internal Server Error
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Error inesperado en la aplicacion: {}", ex.getMessage(), ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado.");
    }


    /* Funciones privadas. */

    // Decide si un valor se muestra o se oculta en el mensaje de error.
    // EJ: Contraseñas, tokens, etc.
    private Object sanitize(String field, Object value){
        if (value == null){
            return null;    // si no hay valor no hay nada que ocultar.
        } else if (SENSITIVE_FIELDS.contains(field.toLowerCase())) {
            return "****";  // campo sensible SI se oculta
        } else {
            return value;   // campo no sensible NO se oculta
        }
    }

    // Para errores de @RequestBody @Valid
    private ValidationError toValidationError(FieldError fe) {
        return new ValidationError(
                fe.getField(),
                sanitize(fe.getField(), fe.getRejectedValue()),
                fe.getCode(),
                fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor invalido"
        );
    }

    // Para errores de @PathVariable/@RequestParam
    private ValidationError toValidationError(ConstraintViolation<?> cv) {
        String campo = cv.getPropertyPath().toString();

        return new ValidationError(
                campo,
                sanitize(campo, cv.getInvalidValue()),
                cv.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                cv.getMessage()
        );
    }

    /* Esta funcion crea el Problem Detail que se creaban tanto en ConstraintViolation y en MethodArgumentNotValid */
    private ProblemDetail buildValidationProblem(
            List<ValidationError> fieldErrors,
            List<GlobalError> globalErrors,
            String detail) {

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);

        if (!fieldErrors.isEmpty()) {
            pd.setProperty("errors", fieldErrors);
        }
        if (!globalErrors.isEmpty()) {
            pd.setProperty("globalErrors", globalErrors);
        }
        return pd;
    }
}
