package com.example.tutorias.service;

import com.example.tutorias.dto.tutoria.CrearTutoriaRequest;
import com.example.tutorias.dto.tutoria.TutoriaResponse;
import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.Materia;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.Sede;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
            tutoria.setSede(Sede.PERGAMINO);
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
        request.setSede(Sede.PERGAMINO);
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
    @Test
    void obtenerTutoriaPorId_Exito_DevuelveTutoriaResponse() {
        // 1. Arrange (Preparar los datos)
        Tutoria tutoriaFalsa = new Tutoria();
        tutoriaFalsa.setId(1L);
        tutoriaFalsa.setNombre("Repaso General");
        tutoriaFalsa.setCupo(10);
        
        // El toResponse suele requerir que la materia y el tutor no sean nulos para sacar el nombre
        Materia materia = new Materia();
        materia.setId(2L);
        materia.setNombre("Matemática");
        tutoriaFalsa.setMateria(materia);
        
        Tutor tutor = new Tutor();
        tutor.setId(3L);
        tutor.setNombre("Agustín");
        tutoriaFalsa.setTutor(tutor);

        // Simulamos que el repositorio encuentra la tutoría
        when(tutoriaRepository.findById(1L)).thenReturn(Optional.of(tutoriaFalsa));
        // Simulamos el conteo de inscriptos para que el mapeo al DTO no explote
        when(tutoriaRepository.countAlumnosByTutoriaId(1L)).thenReturn(2L);

        // 2. Act (Ejecutar el método)
        TutoriaResponse resultado = tutoriaService.obtenerTutoriaPorId(1L);

        // 3. Assert (Comprobar que funcionó)
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Repaso General", resultado.getNombre());
        // Verificamos que el repositorio fue llamado exactamente una vez con el ID 1
        verify(tutoriaRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerTutoriaPorId_NoExiste_LanzaExcepcion() {
        // 1. Arrange
        // Simulamos que el repositorio devuelve un Optional vacío (no encontró nada en la BD)
        when(tutoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // 2 & 3. Act & Assert
        // Verificamos que al buscar ese ID inexistente, el servicio frene todo y lance un 404
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tutoriaService.obtenerTutoriaPorId(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Tutoría no encontrada", exception.getReason());
        
        // Verificamos que al fallar, nunca intentó buscar la cantidad de alumnos inscriptos
        verify(tutoriaRepository, never()).countAlumnosByTutoriaId(anyLong());
    }
    
    @Test
    void buscarTutoriasConFiltros_Exito_DevuelveLista() {
        
        Tutoria tutoriaFalsa = new Tutoria();
        tutoriaFalsa.setId(1L);
        tutoriaFalsa.setNombre("Repaso de Álgebra");
        tutoriaFalsa.setSede(Sede.PERGAMINO);
        tutoriaFalsa.setModalidad(ModalidadTutoria.PRESENCIAL);
        
        Materia materia = new Materia();
        materia.setNombre("Álgebra");
        tutoriaFalsa.setMateria(materia);

        // Simulamos que el repositorio encuentra esta tutoría cuando le pasan cualquier Specification
        when(tutoriaRepository.findAll(any(Specification.class))).thenReturn(List.of(tutoriaFalsa));
        
        // Simulamos el conteo de inscriptos para que no falle el "toResponse"
        when(tutoriaRepository.countAlumnosByTutoriaId(1L)).thenReturn(5L);

        // 2. Act (Ejecutar el método)
        List<TutoriaResponse> resultado = tutoriaService.buscarTutoriasConFiltros("álgebra", Sede.PERGAMINO, ModalidadTutoria.PRESENCIAL);

        // 3. Assert (Comprobar que funcionó)
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Repaso de Álgebra", resultado.get(0).getNombre());
        assertEquals(Sede.PERGAMINO, resultado.get(0).getSede()); // Verifica que el DTO mapeó bien la sede
    }

    @Test
    void buscarTutoriasConFiltros_SinFiltros_TraeTodas() {
        Tutoria tutoria1 = new Tutoria(); tutoria1.setId(1L);
        Tutoria tutoria2 = new Tutoria(); tutoria2.setId(2L);
        
        // Si le pasamos nulls, debería armar una spec vacía y traer todo lo que haya
        when(tutoriaRepository.findAll(any(Specification.class))).thenReturn(List.of(tutoria1, tutoria2));
        when(tutoriaRepository.countAlumnosByTutoriaId(anyLong())).thenReturn(0L);

        List<TutoriaResponse> resultado = tutoriaService.buscarTutoriasConFiltros(null, null, null);

        assertEquals(2, resultado.size());
        verify(tutoriaRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void buscarTutoriasConFiltros_SinResultados_LanzaExcepcion() {
        // Simulamos que la base de datos devuelve una lista vacía
        when(tutoriaRepository.findAll(any(Specification.class))).thenReturn(List.of());

        // Comprobamos que al no encontrar nada, lance un ResponseStatusException (404)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tutoriaService.buscarTutoriasConFiltros("Materia Rara", Sede.JUNIN, ModalidadTutoria.VIRTUAL);
        });

        // Verificamos que sea un código 404 NOT FOUND
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No se encontraron tutorías con los filtros aplicados.", exception.getReason());
    }
}
