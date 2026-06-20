package com.example.tutorias.service;

import com.example.tutorias.dto.avisos.AvisoResponseDTO;
import com.example.tutorias.dto.avisos.CrearAvisoRequestDTO;
import com.example.tutorias.entity.Aviso;
import com.example.tutorias.entity.Tutor;
import com.example.tutorias.entity.Tutoria;
import com.example.tutorias.repository.AlumnoRepository;
import com.example.tutorias.repository.AvisoRepository;
import com.example.tutorias.repository.TutorRepository;
import com.example.tutorias.repository.TutoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvisoServiceTest {

    @Mock
    private AvisoRepository avisoRepository;

    @Mock
    private TutoriaRepository tutoriaRepository;

    @Mock
    private TutorRepository tutorRepository;

    @InjectMocks
    private AvisoServiceImp avisoService;

    @Mock
    private AlumnoRepository alumnoRepository;

    private Tutor tutorMock;
    private Tutoria tutoriaMock;
    private CrearAvisoRequestDTO requestMock;
    private final String EMAIL_TUTOR = "tutor@prueba.com";

    @BeforeEach
    void setUp() {
        tutorMock = new Tutor();
        tutorMock.setId(1L);
        tutorMock.setEmail(EMAIL_TUTOR);
        tutorMock.setNombre("Juan");
        tutorMock.setApellido("Perez");

        tutoriaMock = new Tutoria();
        tutoriaMock.setId(10L);
        tutoriaMock.setNombre("Repaso Álgebra");
        tutoriaMock.setTutor(tutorMock);

        requestMock = new CrearAvisoRequestDTO();
        requestMock.setTutoriaId(10L);
        requestMock.setTitulo("Cambio de aula");
        requestMock.setContenido("Pasamos al aula 5");
    }

    @Test
    void crearAviso_ConMasDe4HorasDeAnticipacion_GuardaAvisoYDevuelveResponse() {
        // Arrange: Configuramos la tutoría para mañana (pasa la regla de las 4 horas)
        tutoriaMock.setFecha(LocalDate.now().plusDays(1)); 
        tutoriaMock.setHoraInicio(LocalTime.of(18, 0));

        when(tutoriaRepository.findById(10L)).thenReturn(Optional.of(tutoriaMock));
        when(tutorRepository.findByEmail(EMAIL_TUTOR)).thenReturn(Optional.of(tutorMock));

        Aviso avisoGuardado = new Aviso();
        avisoGuardado.setId(100L);
        avisoGuardado.setTitulo(requestMock.getTitulo());
        avisoGuardado.setContenido(requestMock.getContenido());
        avisoGuardado.setTutoria(tutoriaMock);
        avisoGuardado.setTutor(tutorMock);
        avisoGuardado.setCreatedAt(LocalDateTime.now());

        when(avisoRepository.save(any(Aviso.class))).thenReturn(avisoGuardado);

        // Act
        AvisoResponseDTO resultado = avisoService.crearAviso(requestMock, EMAIL_TUTOR);

        // Assert
        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("Cambio de aula", resultado.getTitulo());
        verify(avisoRepository, times(1)).save(any(Aviso.class));
    }

    @Test
    void crearAviso_ConMenosDe4Horas_LanzaBadRequest() {
        // Arrange: Configuramos la tutoría para dentro de 1 hora (debe fallar)
        LocalDateTime dentroDeUnaHora = LocalDateTime.now().plusHours(1);
        tutoriaMock.setFecha(dentroDeUnaHora.toLocalDate());
        tutoriaMock.setHoraInicio(dentroDeUnaHora.toLocalTime());

        when(tutoriaRepository.findById(10L)).thenReturn(Optional.of(tutoriaMock));
        when(tutorRepository.findByEmail(EMAIL_TUTOR)).thenReturn(Optional.of(tutorMock));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            avisoService.crearAviso(requestMock, EMAIL_TUTOR);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Es demasiado tarde para enviar el aviso"));
        
        // Verificamos que jamás llegó a guardarse en la BD
        verify(avisoRepository, never()).save(any(Aviso.class));
    }

    // --- TESTS PARA OBTENER AVISOS POR TUTORÍA ---

    @Test
    void obtenerAvisosPorTutoria_TutoriaExiste_DevuelveListaDeAvisos() {
        // Arrange
        when(tutoriaRepository.existsById(10L)).thenReturn(true);
        
        Aviso aviso1 = new Aviso();
        aviso1.setId(1L);
        aviso1.setTitulo("Aviso 1");
        aviso1.setContenido("Contenido 1");
        aviso1.setTutoria(tutoriaMock);
        aviso1.setTutor(tutorMock);
        aviso1.setCreatedAt(LocalDateTime.now());
        
        when(avisoRepository.findByTutoriaIdAndActivoTrue(10L)).thenReturn(List.of(aviso1));

        // Act
        List<AvisoResponseDTO> resultado = avisoService.obtenerAvisosPorTutoria(10L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Aviso 1", resultado.get(0).getTitulo());
        assertEquals(tutoriaMock.getNombre(), resultado.get(0).getNombreTutoria());
    }

    @Test
    void obtenerAvisosPorTutoria_TutoriaNoExiste_LanzaNotFound() {
        // Arrange
        when(tutoriaRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            avisoService.obtenerAvisosPorTutoria(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Tutoría no encontrada", exception.getReason());
        verify(avisoRepository, never()).findByTutoriaIdAndActivoTrue(anyLong());
    }

    //test de getters

    
    @Test
    void obtenerAvisosPorTutorLogueado_TutorExiste_DevuelveLista() {
        // Arrange
        when(tutorRepository.existsByEmail(EMAIL_TUTOR)).thenReturn(true);
        
        Aviso aviso = new Aviso();
        aviso.setId(2L);
        aviso.setTitulo("Aviso del Tutor");
        aviso.setTutoria(tutoriaMock);
        aviso.setTutor(tutorMock);
        
        when(avisoRepository.findByTutor_EmailAndActivoTrue(EMAIL_TUTOR)).thenReturn(List.of(aviso));

        // Act
        List<AvisoResponseDTO> resultado = avisoService.obtenerAvisosPorTutorLogueado(EMAIL_TUTOR);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Aviso del Tutor", resultado.get(0).getTitulo());
    }

    @Test
    void obtenerAvisosPorTutorLogueado_TutorNoExiste_LanzaNotFound() {
        // Arrange
        when(tutorRepository.existsByEmail("fantasma@comunidad.unnoba.edu.ar")).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            avisoService.obtenerAvisosPorTutorLogueado("fantasma@comunidad.unnoba.edu.ar");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

@Test
    void obtenerAvisosPorAlumnoLogueado_Exito_DevuelveListaCruzada() {
        // Arrange
        String emailAlumno = "alumno@comunidad.unnoba.edu.ar";
        
        // AGREGAR ESTA LÍNEA si tu servicio ahora valida la existencia del alumno por email:
        when(alumnoRepository.existsByEmail(emailAlumno)).thenReturn(true);

        Aviso aviso = new Aviso();
        aviso.setId(3L);
        aviso.setTitulo("Aviso para el alumno");
        aviso.setTutoria(tutoriaMock);
        aviso.setTutor(tutorMock);

        when(avisoRepository.findByTutoria_Alumnos_EmailAndActivoTrue(emailAlumno))
                .thenReturn(List.of(aviso));

    
        List<AvisoResponseDTO> resultado = avisoService.obtenerAvisosPorAlumnoLogueado(emailAlumno);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(avisoRepository, times(1)).findByTutoria_Alumnos_EmailAndActivoTrue(emailAlumno);
    }
}