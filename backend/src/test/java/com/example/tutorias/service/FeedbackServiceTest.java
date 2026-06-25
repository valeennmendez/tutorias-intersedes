package com.example.tutorias.service;

import com.example.tutorias.dto.feedback.CrearFeedbackRequestDTO;
import com.example.tutorias.entity.*;
import com.example.tutorias.repository.FeedbackRepository;
import com.example.tutorias.repository.InscripcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private Inscripcion inscripcionMock;
    private Alumno alumnoMock;
    private Tutoria tutoriaMock;
    private CrearFeedbackRequestDTO requestMock;
    private final String EMAIL_ALUMNO = "alumno@test.com";

    @BeforeEach
    void setUp() {
        alumnoMock = new Alumno();
        alumnoMock.setEmail(EMAIL_ALUMNO);

        tutoriaMock = new Tutoria();
        // Le ponemos una fecha del PASADO para que permita evaluar
        tutoriaMock.setFecha(LocalDate.now().minusDays(1)); 
        tutoriaMock.setHoraInicio(LocalTime.of(10, 0));

        inscripcionMock = new Inscripcion();
        inscripcionMock.setId(1L);
        inscripcionMock.setAlumno(alumnoMock);
        inscripcionMock.setTutoria(tutoriaMock);

        requestMock = new CrearFeedbackRequestDTO();
        requestMock.setCalificacion(5);
        requestMock.setComentarios("Excelente clase");
        requestMock.setEsAnonimo(false);
    }

    @Test
    void crearFeedback_Exito() {
        // Arrange
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcionMock));
        
        // Act
        feedbackService.crearFeedback(1L, EMAIL_ALUMNO, requestMock);

        // Assert
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void crearFeedback_NoEsElDueño_LanzaForbidden() {
        // Arrange
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcionMock));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            feedbackService.crearFeedback(1L, "impostor@test.com", requestMock);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(feedbackRepository, never()).save(any());
    }

    @Test
    void crearFeedback_ClaseAunNoOcurrio_LanzaBadRequest() {
        // Arrange
        // Le ponemos una fecha del FUTURO
        tutoriaMock.setFecha(LocalDate.now().plusDays(1)); 
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcionMock));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            feedbackService.crearFeedback(1L, EMAIL_ALUMNO, requestMock);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("No podés evaluar una tutoría que todavía no ocurrió.", exception.getReason());
    }
}