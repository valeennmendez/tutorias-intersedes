package com.example.tutorias.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.tutorias.dto.avisos.AvisoResponseDTO;
import com.example.tutorias.dto.avisos.CrearAvisoRequestDTO;
import com.example.tutorias.service.AvisoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/avisos")
public class AvisoController {    
    private final AvisoService avisoService;
    
    public AvisoController(AvisoService avisoService) {
        this.avisoService = avisoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TUTOR')") // Solo los usuarios con el rol de TUTOR pueden acceder a este endpoint
    public ResponseEntity<AvisoResponseDTO> crearAviso(@Valid @RequestBody CrearAvisoRequestDTO request, java.security.Principal principal) 
        {
            String emailTutorLogueado = principal.getName(); // en el jwt nuestro el email es el username, por eso lo obtenemos así
            AvisoResponseDTO response = avisoService.crearAviso(request, emailTutorLogueado);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tutoria/{tutoriaId}")
    @PreAuthorize("hasRole('TUTOR') or hasRole('ALUMNO') or hasRole('ADMIN')") // Tanto tutores como alumnos pueden ver los avisos de una tutoria
    public ResponseEntity<List<AvisoResponseDTO>> obtenerAvisosPorTutoria(@PathVariable Long tutoriaId) {
        List<AvisoResponseDTO> avisos = avisoService.obtenerAvisosPorTutoria(tutoriaId);
        return ResponseEntity.ok(avisos);
    }

    // Este endpoint es para que un tutor pueda ver todos los avisos de todas sus tutorias, el matcheo se hace por email que viene del token de seguridad
    @GetMapping("/mis-avisos")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<List<AvisoResponseDTO>> obtenerAvisosMisTutorias(Principal principal) {
        String emailAlumno = principal.getName();
        List<AvisoResponseDTO> avisos = avisoService.obtenerAvisosPorAlumnoLogueado(emailAlumno);
        return ResponseEntity.ok(avisos);
    }

    //Este endpoint es para que un tutor pueda ver todos los avisos que él mismo publicó, misma idea de matcheo por email del token que en el anterior
    @GetMapping("/mis-avisos-enviados")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<List<AvisoResponseDTO>> obtenerAvisosPublicados(Principal principal) {
        String emailTutor = principal.getName();
        List<AvisoResponseDTO> avisos = avisoService.obtenerAvisosPorTutorLogueado(emailTutor);
        return ResponseEntity.ok(avisos);
    }

    @DeleteMapping("/{avisoId}")
    @PreAuthorize("hasRole('TUTOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarAviso(@PathVariable Long avisoId) {
        avisoService.eliminarAviso(avisoId);
        return ResponseEntity.noContent().build(); // Devuelve 204 indicando que se borró con éxito
    }
    
}
