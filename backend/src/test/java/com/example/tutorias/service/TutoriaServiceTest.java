package com.example.tutorias.service;

import com.example.tutorias.dto.tutoria.CrearTutoriaRequest;
import com.example.tutorias.dto.tutoria.TutoriaResponse;
import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.Materia;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.Tutor;
import com.example.tutorias.entity.Tutoria;
import com.example.tutorias.repository.MateriaRepository;
import com.example.tutorias.repository.TutorRepository;
import com.example.tutorias.repository.TutoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutoriaServiceTest {

    @Mock
    private TutoriaRepository tutoriaRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private MateriaRepository materiaRepository;

    private TutoriaService tutoriaService;

    @BeforeEach
    void setUp() {
        tutoriaService = new TutoriaService(tutoriaRepository, tutorRepository, materiaRepository);
    }

    @Test
    void crearTutoriaCreaTutoriaConTutorAprobadoYMateriaExistente() {
        CrearTutoriaRequest request = requestVirtual();
        Tutor tutor = tutorAprobado();
        Materia materia = materia();

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(materiaRepository.findById(2L)).thenReturn(Optional.of(materia));
        when(tutoriaRepository.existsByTutorIdAndFechaAndEstadoAndHoraInicioBeforeAndHoraFinAfter(
                1L,
                request.getFecha(),
                EstadoTutoria.ACTIVA,
                request.getHoraFin(),
                request.getHoraInicio()
        )).thenReturn(false);
        when(tutoriaRepository.save(any(Tutoria.class))).thenAnswer(invocation -> {
            Tutoria tutoria = invocation.getArgument(0);
            tutoria.setId(10L);
            return tutoria;
        });
        when(tutoriaRepository.countAlumnosByTutoriaId(10L)).thenReturn(0L);

        TutoriaResponse response = tutoriaService.crearTutoria(request);

        assertEquals(10L, response.getId());
        assertEquals("Matematica I", response.getNombre());
        assertEquals(1L, response.getTutorId());
        assertEquals(2L, response.getMateriaId());
        assertEquals(0L, response.getCantidadInscriptos());
    }

    @Test
    void crearTutoriaRechazaTutorNoAprobado() {
        CrearTutoriaRequest request = requestVirtual();
        Tutor tutor = tutorAprobado();
        tutor.setEstado(false);

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));

        assertThrows(ResponseStatusException.class, () -> tutoriaService.crearTutoria(request));
    }

    @Test
    void crearTutoriaRechazaHorarioSolapadoDelTutor() {
        CrearTutoriaRequest request = requestVirtual();

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutorAprobado()));
        when(materiaRepository.findById(2L)).thenReturn(Optional.of(materia()));
        when(tutoriaRepository.existsByTutorIdAndFechaAndEstadoAndHoraInicioBeforeAndHoraFinAfter(
                1L,
                request.getFecha(),
                EstadoTutoria.ACTIVA,
                request.getHoraFin(),
                request.getHoraInicio()
        )).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> tutoriaService.crearTutoria(request));
    }

    private CrearTutoriaRequest requestVirtual() {
        CrearTutoriaRequest request = new CrearTutoriaRequest();
        request.setNombre("Matematica I");
        request.setDescripcion("Repaso para parcial");
        request.setTutorId(1L);
        request.setMateriaId(2L);
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(18, 0));
        request.setHoraFin(LocalTime.of(20, 0));
        request.setModalidad(ModalidadTutoria.VIRTUAL);
        request.setLinkVirtual("https://meet.google.com/test");
        request.setCupo(20);
        return request;
    }

    private Tutor tutorAprobado() {
        Tutor tutor = new Tutor();
        tutor.setId(1L);
        tutor.setNombre("Tutor Test");
        tutor.setEstado(true);
        return tutor;
    }

    private Materia materia() {
        Materia materia = new Materia();
        materia.setId(2L);
        materia.setNombre("Matematica I");
        return materia;
    }
}
