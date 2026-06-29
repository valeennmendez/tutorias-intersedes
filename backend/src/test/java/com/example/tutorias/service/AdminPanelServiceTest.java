package com.example.tutorias.service;

import com.example.tutorias.dto.tutoria.TutoriaResponse;
import com.example.tutorias.dto.admin.PostulacionAdminDTO;
import com.example.tutorias.dto.admin.TutorAdminDTO;
import com.example.tutorias.entity.*;
import com.example.tutorias.repository.PostulacionTutorRepository;
import com.example.tutorias.repository.TutorRepository;
import com.example.tutorias.repository.TutoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminPanelServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private PostulacionTutorRepository postulacionRepository;

    @Mock
    private TutoriaRepository tutoriaRepository;

    @InjectMocks
    private AdminPanelService adminPanelService;

    private Pageable pageableMock;

    @BeforeEach
    void setUp() {
        pageableMock = PageRequest.of(0, 10);
    }

    @Test
    void obtenerTutoresAprobados_DebeMapearCorrectamente() {
        // Arrange
        Tutor tutor = new Tutor();
        tutor.setId(1L);
        tutor.setNombre("Juan");
        tutor.setApellido("Perez");
        tutor.setEmail("juan@tutor.com");
        tutor.setCuit("20-12345678-9");
        tutor.setTitulo("Ingeniero en Sistemas");

        Page<Tutor> pageTutor = new PageImpl<>(List.of(tutor));
        when(tutorRepository.findByEstadoTrue(pageableMock)).thenReturn(pageTutor);

        // Act
        Page<TutorAdminDTO> resultado = adminPanelService.obtenerTutoresAprobados(pageableMock);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        TutorAdminDTO dto = resultado.getContent().get(0);
        assertEquals("Juan Perez", dto.getNombreCompleto());
        assertEquals("juan@tutor.com", dto.getEmail());
        verify(tutorRepository, times(1)).findByEstadoTrue(pageableMock);
    }

    @Test
    void obtenerPostulacionesPendientes_DebeMapearConDatosDelAlumno() {
        // Arrange
        Alumno alumno = new Alumno();
        alumno.setNombre("Carlos");
        alumno.setApellido("Gomez");
        alumno.setEmail("carlos@alumno.com");

        Materia materia = new Materia();
        materia.setNombre("Análisis Matemático");

        PostulacionTutor postulacion = new PostulacionTutor();
        postulacion.setId(5L);
        postulacion.setPostulante(alumno);
        postulacion.setMateria(materia);
        postulacion.setEstado(PostulacionTutorEstado.PENDIENTE);
        postulacion.setJustificacion("Me gusta enseñar");
        postulacion.setNotaAprobacion(9.0);
        postulacion.setSedePreferencia("Pergamino");
        postulacion.setModalidadPreferencia(ModalidadTutoria.PRESENCIAL);

        Page<PostulacionTutor> pagePostulacion = new PageImpl<>(List.of(postulacion));
        when(postulacionRepository.findByEstado(PostulacionTutorEstado.PENDIENTE, pageableMock)).thenReturn(pagePostulacion);

        // Act
        Page<PostulacionAdminDTO> resultado = adminPanelService.obtenerPostulacionesPendientes(pageableMock);

        // Assert
        assertNotNull(resultado);
        PostulacionAdminDTO dto = resultado.getContent().get(0);
        assertEquals("Carlos Gomez", dto.getPostulanteNombre());
        assertEquals("carlos@alumno.com", dto.getPostulanteEmail());
        assertEquals("Análisis Matemático", dto.getMateria());
        verify(postulacionRepository, times(1)).findByEstado(PostulacionTutorEstado.PENDIENTE, pageableMock);
    }

    @Test
    void obtenerTutoriasActivas_DebeUsarTutoriaResponseExitosamente() {
        // Arrange
        Tutor tutor = new Tutor();
        tutor.setId(2L);
        tutor.setNombre("Sonia");
        tutor.setApellido("Rojas");

        Materia materia = new Materia();
        materia.setId(10L);
        materia.setNombre("Programación");

        Tutoria tutoria = new Tutoria();
        tutoria.setId(1L);
        tutoria.setNombre("Taller de Java");
        tutoria.setDescripcion("Clase práctica");
        tutoria.setFecha(LocalDate.now().plusDays(5));
        tutoria.setHoraInicio(LocalTime.of(18, 0));
        tutoria.setHoraFin(LocalTime.of(20, 0));
        tutoria.setCupo(15);
        tutoria.setModalidad(ModalidadTutoria.VIRTUAL);
        tutoria.setEstado(EstadoTutoria.ACTIVA);
        tutoria.setTutor(tutor);
        tutoria.setMateria(materia);

        Page<Tutoria> pageTutoria = new PageImpl<>(List.of(tutoria));
        when(tutoriaRepository.findByEstado(EstadoTutoria.ACTIVA, pageableMock)).thenReturn(pageTutoria);

        // Act
        Page<TutoriaResponse> resultado = adminPanelService.obtenerTutoriasActivas(pageableMock);

        // Assert
        assertNotNull(resultado);
        TutoriaResponse dto = resultado.getContent().get(0);
        assertEquals("Taller de Java", dto.getNombre());
        assertEquals("Sonia Rojas", dto.getTutorNombre());
        assertEquals("Programación", dto.getMateriaNombre());
        verify(tutoriaRepository, times(1)).findByEstado(EstadoTutoria.ACTIVA, pageableMock);
    }
}