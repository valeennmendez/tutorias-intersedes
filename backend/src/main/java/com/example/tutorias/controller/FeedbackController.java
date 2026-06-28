package com.example.tutorias.controller;

import com.example.tutorias.dto.feedback.CrearFeedbackRequestDTO;
import com.example.tutorias.dto.feedback.FeedbackResponseDTO;
import com.example.tutorias.service.FeedbackService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // POST: Crear (el que hicimos antes)
    @PostMapping("/inscripcion/{inscripcionId}")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<Void> dejarFeedback(@PathVariable Long inscripcionId, 
                                              @RequestBody CrearFeedbackRequestDTO request, 
                                              Principal principal) {
        feedbackService.crearFeedback(inscripcionId, principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // PUT: Editar feedback existente
    @PutMapping("/{feedbackId}")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<FeedbackResponseDTO> editarFeedback(@PathVariable Long feedbackId, 
                                                              @RequestBody CrearFeedbackRequestDTO request, 
                                                              Principal principal) {
        FeedbackResponseDTO response = feedbackService.editarFeedback(feedbackId, principal.getName(), request);
        return ResponseEntity.ok(response);
    }

    // Ver feedbacks de una tutoria específica (Público para logueados)
    @GetMapping("/tutoria/{tutoriaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<FeedbackResponseDTO>> obtenerFeedbacksPorTutoria(
            @PathVariable Long tutoriaId, 
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<FeedbackResponseDTO> page = feedbackService.obtenerFeedbacksPorTutoria(tutoriaId, pageable);
        return ResponseEntity.ok(page);
    }

    // ver feedbacks de un tutor específico (Público para logueados)
    @GetMapping("/tutor/{tutorId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<FeedbackResponseDTO>> obtenerFeedbacksPorTutor(
            @PathVariable Long tutorId, 
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<FeedbackResponseDTO> page = feedbackService.obtenerFeedbacksPorTutor(tutorId, pageable);
        return ResponseEntity.ok(page);
    }

    // Ver mis feedbacks (Alumno)
    @GetMapping("/mis-feedbacks")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<Page<FeedbackResponseDTO>> obtenerMisFeedbacks(
            Principal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<FeedbackResponseDTO> page = feedbackService.obtenerMisFeedbacks(principal.getName(), pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/promedio-estrellas/tutor/{tutorId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Double> obtenerPromedioEstrellasPorTutor(@PathVariable Long tutorId) {
        Double promedio = feedbackService.obtenerPromedioEstrellasPorTutor(tutorId);
        return ResponseEntity.ok(promedio);
    }

}