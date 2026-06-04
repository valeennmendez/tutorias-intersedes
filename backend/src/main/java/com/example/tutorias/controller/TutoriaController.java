package com.example.tutorias.controller;

import com.example.tutorias.dto.tutoria.CantidadInscriptosResponse;
import com.example.tutorias.dto.tutoria.CrearTutoriaRequest;
import com.example.tutorias.dto.tutoria.TutoriaResponse;
import com.example.tutorias.service.TutoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tutorias")
public class TutoriaController {

    private final TutoriaService tutoriaService;

    public TutoriaController(TutoriaService tutoriaService) {
        this.tutoriaService = tutoriaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TutoriaResponse crearTutoria(@Valid @RequestBody CrearTutoriaRequest request) {
        return tutoriaService.crearTutoria(request);
    }

    @GetMapping
    public List<TutoriaResponse> obtenerTutorias() {
        return tutoriaService.obtenerTutorias();
    }

    @GetMapping("/alumno/{alumnoId}")
    public List<TutoriaResponse> obtenerTutoriasPorAlumno(@PathVariable Long alumnoId) {
        return tutoriaService.obtenerTutoriasPorAlumno(alumnoId);
    }

    @GetMapping("/{tutoriaId}/cantidad-inscriptos")
    public CantidadInscriptosResponse obtenerCantidadInscriptos(@PathVariable Long tutoriaId) {
        long cantidad = tutoriaService.obtenerCantidadInscriptos(tutoriaId);
        return new CantidadInscriptosResponse(tutoriaId, cantidad);
    }
}
