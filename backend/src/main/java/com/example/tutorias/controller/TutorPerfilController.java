package com.example.tutorias.controller;

import com.example.tutorias.dto.tutor.PerfilTutorResponseDTO;
import com.example.tutorias.service.TutorPerfilService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/perfil-tutor")
public class TutorPerfilController {

    private final TutorPerfilService tutorPerfilService;

    public TutorPerfilController(TutorPerfilService tutorPerfilService) {
        this.tutorPerfilService = tutorPerfilService;
    }

    @GetMapping("/{tutorId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PerfilTutorResponseDTO> obtenerPerfil(@PathVariable Long tutorId) {
        return ResponseEntity.ok(tutorPerfilService.obtenerPerfil(tutorId));
    }
}
