package com.example.tutorias.service;

import com.example.tutorias.dto.tutor.PerfilTutorResponseDTO;
import com.example.tutorias.entity.Alumno;
import com.example.tutorias.entity.Carrera;
import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.Feedback;
import com.example.tutorias.entity.Inscripcion;
import com.example.tutorias.entity.Materia;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.Sede;
import com.example.tutorias.entity.Tutor;
import com.example.tutorias.entity.Tutoria;
import com.example.tutorias.repository.FeedbackRepository;
import com.example.tutorias.repository.TutorRepository;
import com.example.tutorias.repository.TutoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorPerfilServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private TutoriaRepository tutoriaRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    private TutorPerfilService tutorPerfilService;

    @BeforeEach
    void setUp() {
        tutorPerfilService = new TutorPerfilService(tutorRepository, tutoriaRepository, feedbackRepository);
    }

    @Test
    void obtenerPerfilDevuelveDashboardDelTutorAprobado() {
        Tutor tutor = tutorAprobado();
        Tutoria tutoria = tutoriaActiva(tutor, materia(2L, "Matematica I"));
        Feedback feedback = feedback(tutoria);

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(tutoriaRepository.findByTutorIdAndEstadoOrderByFechaAscHoraInicioAsc(1L, EstadoTutoria.ACTIVA))
                .thenReturn(List.of(tutoria));
        when(feedbackRepository.obtenerPromedioEstrellasPorTutor(1L)).thenReturn(4.5);
        when(feedbackRepository.countByInscripcion_Tutoria_Tutor_Id(1L)).thenReturn(2L);
        when(feedbackRepository.findByInscripcion_Tutoria_Tutor_Id(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(feedback)));

        PerfilTutorResponseDTO perfil = tutorPerfilService.obtenerPerfil(1L);

        assertEquals(1L, perfil.getTutorId());
        assertEquals("Ada Lovelace", perfil.getNombreCompleto());
        assertEquals("Ingenieria en Sistemas", perfil.getCarrera());
        assertEquals(4.5, perfil.getPromedioCalificacion());
        assertEquals(2L, perfil.getCantidadResenas());
        assertEquals(1, perfil.getCantidadTutoriasActivas());
        assertEquals(2, perfil.getMaterias().size());
        assertEquals(1, perfil.getTutoriasActivas().size());
        assertEquals(1, perfil.getResenasRecientes().size());
    }

    @Test
    void obtenerPerfilRechazaTutorNoAprobado() {
        Tutor tutor = tutorAprobado();
        tutor.setEstadoTutor(false);

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tutorPerfilService.obtenerPerfil(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(tutoriaRepository, never()).findByTutorIdAndEstadoOrderByFechaAscHoraInicioAsc(any(), any());
        verify(feedbackRepository, never()).obtenerPromedioEstrellasPorTutor(any());
    }

    @Test
    void obtenerPerfilRechazaTutorInexistente() {
        when(tutorRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tutorPerfilService.obtenerPerfil(99L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private Tutor tutorAprobado() {
        Tutor tutor = new Tutor();
        tutor.setId(1L);
        tutor.setNombre("Ada");
        tutor.setApellido("Lovelace");
        tutor.setEmail("ada@test.com");
        tutor.setTitulo("Analista de Sistemas");
        tutor.setEstadoTutor(true);
        tutor.setCarrera(new Carrera("Ingenieria en Sistemas"));
        tutor.setMaterias(Set.of(materia(2L, "Matematica I"), materia(3L, "Programacion I")));
        return tutor;
    }

    private Materia materia(Long id, String nombre) {
        Materia materia = new Materia();
        materia.setId(id);
        materia.setNombre(nombre);
        return materia;
    }

    private Tutoria tutoriaActiva(Tutor tutor, Materia materia) {
        Tutoria tutoria = new Tutoria();
        tutoria.setId(10L);
        tutoria.setNombre("Parcial algebra");
        tutoria.setFecha(LocalDate.now().plusDays(2));
        tutoria.setHoraInicio(LocalTime.of(18, 0));
        tutoria.setHoraFin(LocalTime.of(20, 0));
        tutoria.setModalidad(ModalidadTutoria.VIRTUAL);
        tutoria.setEstado(EstadoTutoria.ACTIVA);
        tutoria.setSede(Sede.PERGAMINO);
        tutoria.setTutor(tutor);
        tutoria.setMateria(materia);
        return tutoria;
    }

    private Feedback feedback(Tutoria tutoria) {
        Alumno alumno = new Alumno();
        alumno.setNombre("Grace");
        alumno.setApellido("Hopper");

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(20L);
        inscripcion.setTutoria(tutoria);
        inscripcion.setAlumno(alumno);

        Feedback feedback = new Feedback();
        feedback.setId(30L);
        feedback.setInscripcion(inscripcion);
        feedback.setCalificacion(5);
        feedback.setComentarios("Muy clara la explicacion");
        feedback.setEsAnonimo(false);
        feedback.setCreatedAt(LocalDateTime.now());
        return feedback;
    }
}
