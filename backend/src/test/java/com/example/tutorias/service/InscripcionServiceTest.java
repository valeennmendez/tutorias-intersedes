package com.example.tutorias.service;

import com.example.tutorias.dto.inscripcion.InscripcionResponseDTO;
import com.example.tutorias.entity.*;
import com.example.tutorias.repository.AlumnoRepository;
import com.example.tutorias.repository.InscripcionRepository;
import com.example.tutorias.repository.TutoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private TutoriaRepository tutoriaRepository;

    @Mock
    private AlumnoRepository alumnoRepository;

    @InjectMocks
    private InscripcionService inscripcionService;

    private Tutoria tutoriaMock;
    private Alumno alumnoMock;
    private Tutor tutorMock;
    private final String EMAIL_ALUMNO = "alumno@test.com";

    @BeforeEach
    void setUp() {
        tutorMock = new Tutor();
        tutorMock.setNombre("Juan");
        tutorMock.setApellido("Perez");

        tutoriaMock = new Tutoria();
        tutoriaMock.setId(1L);
        tutoriaMock.setNombre("Matemática");
        tutoriaMock.setCupo(5);
        tutoriaMock.setTutor(tutorMock);

        alumnoMock = new Alumno();
        alumnoMock.setId(10L);
        alumnoMock.setEmail(EMAIL_ALUMNO);
    }

    @Test
    void inscribirAlumno_Exito_NuevaInscripcion() {
        // Arrange
        when(tutoriaRepository.findById(1L)).thenReturn(Optional.of(tutoriaMock));
        when(alumnoRepository.findByEmail(EMAIL_ALUMNO)).thenReturn(Optional.of(alumnoMock));
        when(inscripcionRepository.findByTutoriaIdAndAlumnoEmail(1L, EMAIL_ALUMNO)).thenReturn(Optional.empty());
        when(inscripcionRepository.countByTutoriaIdAndStatus(1L, InscripcionStatus.ACTIVA)).thenReturn(2L); // Hay lugar (2/5)
        
        Inscripcion inscripcionGuardada = new Inscripcion();
        inscripcionGuardada.setId(100L);
        inscripcionGuardada.setTutoria(tutoriaMock);
        inscripcionGuardada.setAlumno(alumnoMock);
        inscripcionGuardada.setStatus(InscripcionStatus.ACTIVA);
        
        when(inscripcionRepository.save(any(Inscripcion.class))).thenReturn(inscripcionGuardada);

        // Act
        InscripcionResponseDTO response = inscripcionService.inscribirAlumno(1L, EMAIL_ALUMNO);

        // Assert
        assertNotNull(response);
        assertEquals("ACTIVA", response.getStatus());
        assertEquals("Matemática", response.getNombreTutoria());
        verify(inscripcionRepository).save(any(Inscripcion.class));
    }

    @Test
    void inscribirAlumno_SinCupo_LanzaConflicto() {
        // Arrange
        when(tutoriaRepository.findById(1L)).thenReturn(Optional.of(tutoriaMock));
        when(alumnoRepository.findByEmail(EMAIL_ALUMNO)).thenReturn(Optional.of(alumnoMock));
        when(inscripcionRepository.findByTutoriaIdAndAlumnoEmail(1L, EMAIL_ALUMNO)).thenReturn(Optional.empty());
        
        // Simulamos que ya hay 5 inscriptos (cupo lleno)
        when(inscripcionRepository.countByTutoriaIdAndStatus(1L, InscripcionStatus.ACTIVA)).thenReturn(5L);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            inscripcionService.inscribirAlumno(1L, EMAIL_ALUMNO);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    void cancelarInscripcion_Exito() {
        // Arrange
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setStatus(InscripcionStatus.ACTIVA);
        when(inscripcionRepository.findByTutoriaIdAndAlumnoEmail(1L, EMAIL_ALUMNO))
                .thenReturn(Optional.of(inscripcion));

        // Act
        inscripcionService.cancelarInscripcion(1L, EMAIL_ALUMNO);

        // Assert
        assertEquals(InscripcionStatus.CANCELADA, inscripcion.getStatus());
        verify(inscripcionRepository).save(inscripcion);
    }
}