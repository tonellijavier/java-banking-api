package com.tonelli.banking_api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

// @RestControllerAdvice intercepta todas las excepciones que lanzan los Controllers
// En lugar de devolver un error 500 genérico, devolvemos respuestas claras y útiles
// Es el equivalente al try/catch global de la aplicación
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura RuntimeException — que es lo que lanzamos en el Service
    // cuando no encontramos un cliente, saldo insuficiente, etc.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {

        // Detectamos el tipo de error por el mensaje para devolver el HTTP status correcto
        HttpStatus status;
        if (ex.getMessage().contains("no encontrado")) {
            status = HttpStatus.NOT_FOUND;          // 404 — el recurso no existe
        } else if (ex.getMessage().contains("insuficiente") ||
                ex.getMessage().contains("mayor a cero") ||
                ex.getMessage().contains("no habilitado")) {
            status = HttpStatus.BAD_REQUEST;        // 400 — el request tiene un problema
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 500 — error inesperado
        }

        // Devolvemos un JSON con información útil para el cliente
        Map<String, Object> error = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    status.value(),
                "error",     status.getReasonPhrase(),
                "message",   ex.getMessage()
        );

        return ResponseEntity.status(status).body(error);
    }
}