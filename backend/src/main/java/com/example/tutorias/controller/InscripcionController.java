package com.example.tutorias.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutorias.dto.inscripcion.InscripcionResponseDTO;
import com.example.tutorias.service.InscripcionService;

@RestController
@RequestMapping("/inscripciones")
public class InscripcionController {
    
    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping("/tutoria/{tutoriaId}")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<InscripcionResponseDTO> inscribirse(@PathVariable Long tutoriaId, Principal principal) {
        InscripcionResponseDTO response = inscripcionService.inscribirAlumno(tutoriaId, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    

    // Endpoint para darse de baja
    @PutMapping("/tutoria/{tutoriaId}/cancelar")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<Void> darseDeBaja(@PathVariable Long tutoriaId, Principal principal) {
        inscripcionService.cancelarInscripcion(tutoriaId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // Endpoint para que el Alumno vea sus propias inscripciones
    @GetMapping("/mis-inscripciones")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<List<InscripcionResponseDTO>> obtenerMisInscripciones(Principal principal) {
        List<InscripcionResponseDTO> inscripciones = inscripcionService.obtenerMisInscripciones(principal.getName());
        return ResponseEntity.ok(inscripciones);
    }

    // Endpoint para que el Tutor pase lista
    @GetMapping("/tutoria/{tutoriaId}")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<List<InscripcionResponseDTO>> obtenerInscripcionesDeTutoria(@PathVariable Long tutoriaId, Principal principal) {
        List<InscripcionResponseDTO> inscripciones = inscripcionService.obtenerInscripcionesPorTutoria(tutoriaId, principal.getName());
        return ResponseEntity.ok(inscripciones);
    }
}
