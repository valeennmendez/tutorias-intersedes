package com.example.tutorias.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler  {
    
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> handleReglaNegocioException(ReglaNegocioException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }
    // Manejador para errores de validación de datos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errores.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }
    // si es un 500, es un error del servidor, no del cliente, pero lo manejamos para evitar que se expongan detalles innecesarios
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenerico(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Ocurrió un error inesperado.");
        response.put("detalle", ex.getMessage()); 
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
