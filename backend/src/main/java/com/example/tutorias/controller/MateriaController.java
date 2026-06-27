package com.example.tutorias.controller;

import com.example.tutorias.dto.materia.MateriaResponse;
import com.example.tutorias.service.MateriaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/materias")
public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @GetMapping("/tutor/{tutorId}")
    public List<MateriaResponse> obtenerMateriasPorTutor(@PathVariable Long tutorId) {
        return materiaService.obtenerMateriasPorTutor(tutorId);
    }

    @GetMapping
    public List<MateriaResponse> obtenerTodasMaterias() {
        return materiaService.obtenerTodasMaterias();
    }
}
