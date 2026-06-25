package com.example.tutorias.service;


import com.example.tutorias.dto.feedback.CrearFeedbackRequestDTO;
import com.example.tutorias.dto.feedback.FeedbackResponseDTO;
import com.example.tutorias.entity.Feedback;
import com.example.tutorias.entity.Inscripcion;
import com.example.tutorias.repository.FeedbackRepository;
import com.example.tutorias.repository.InscripcionRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final InscripcionRepository inscripcionRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, InscripcionRepository inscripcionRepository) {
        this.feedbackRepository = feedbackRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    @Transactional
    public void crearFeedback(Long inscripcionId, String emailAlumno, CrearFeedbackRequestDTO request) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada."));

        // 1. Validar que la inscripción sea de este alumno
        if (!inscripcion.getAlumno().getEmail().equals(emailAlumno)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta inscripción no te pertenece.");
        }

        // 2. Validar que no tenga feedback ya cargado
        if (inscripcion.getFeedback() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya dejaste feedback para esta tutoría.");
        }

        // 3. Validar que la tutoría ya haya terminado
        LocalDate fechaClase = inscripcion.getTutoria().getFecha();
        LocalTime horaClase = inscripcion.getTutoria().getHoraInicio();
        
        if (fechaClase.isAfter(LocalDate.now()) || 
           (fechaClase.isEqual(LocalDate.now()) && horaClase.isAfter(LocalTime.now()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No podés evaluar una tutoría que todavía no ocurrió.");
        }

        // 4. Crear y guardar
        Feedback feedback = new Feedback();
        feedback.setInscripcion(inscripcion);
        feedback.setCalificacion(request.getCalificacion());
        feedback.setComentarios(request.getComentarios());
        
        feedback.setEsAnonimo(request.isEsAnonimo());

        feedbackRepository.save(feedback);
    }

    @Transactional
    public FeedbackResponseDTO editarFeedback(Long feedbackId, String emailAlumno, CrearFeedbackRequestDTO request) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback no encontrado."));

        // Validar que sea el dueño del feedback
        if (!feedback.getInscripcion().getAlumno().getEmail().equals(emailAlumno)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No podés editar un feedback que no es tuyo.");
        }

        // Actualizamos los datos
        feedback.setCalificacion(request.getCalificacion());
        feedback.setComentarios(request.getComentarios());
        feedback.setEsAnonimo(request.isEsAnonimo());

        return FeedbackResponseDTO.from(feedbackRepository.save(feedback));
    }

    @Transactional(readOnly = true)
    public Page<FeedbackResponseDTO> obtenerMisFeedbacks(String emailAlumno, Pageable pageable) {
        return feedbackRepository.findByInscripcionAlumnoEmail(emailAlumno, pageable)
                .map(FeedbackResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<FeedbackResponseDTO> obtenerFeedbacksPorTutor(Long tutorId, Pageable pageable) {
        return feedbackRepository.findByInscripcion_Tutoria_Tutor_Id(tutorId, pageable)
                .map(FeedbackResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<FeedbackResponseDTO> obtenerFeedbacksPorTutoria(Long tutoriaId, Pageable pageable) {
        return feedbackRepository.findByInscripcionTutoriaId(tutoriaId, pageable)
                .map(FeedbackResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public Double obtenerPromedioEstrellasPorTutor(Long tutorId) {
        return feedbackRepository.obtenerPromedioEstrellasPorTutor(tutorId);
    }
}