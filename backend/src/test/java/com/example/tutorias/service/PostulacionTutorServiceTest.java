package com.example.tutorias.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.example.tutorias.entity.Alumno;
import com.example.tutorias.entity.Materia;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.PostulacionTutor;
import com.example.tutorias.entity.PostulacionTutorEstado;
import com.example.tutorias.exception.ReglaNegocioException;
import com.example.tutorias.repository.AlumnoRepository;
import com.example.tutorias.repository.MateriaRepository;
import com.example.tutorias.repository.PostulacionTutorRepository;
import com.example.tutorias.dto.postulaciones.PostulacionTutorResponseDTO;

@ExtendWith(MockitoExtension.class)
public class PostulacionTutorServiceTest {

    @Mock
    private AlumnoRepository alumnoRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private PostulacionTutorRepository postulacionTutorRepository;

    @InjectMocks
    private PostulacionTutorServiceImp postulacionTutorService;

    @Test
    void registrarPostulacion_Exito() {
        MockMultipartFile pdf = new MockMultipartFile("archivo", "test.pdf", "application/pdf", "data".getBytes());
        Alumno alumno = new Alumno(); alumno.setId(1L);
        Materia materia = new Materia(); materia.setId(1L); materia.setNombre("Programación");

        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));
        when(postulacionTutorRepository.findByPostulanteId(1L)).thenReturn(List.of());
        when(fileStorageService.guardarArchivo(pdf)).thenReturn("ruta/test.pdf");
        
        // Simulamos guardar la postulación y le asignamos un ID inventado, ya que normalmente lo haría la bd
        PostulacionTutor guardada = new PostulacionTutor();
        guardada.setId(1L); // Le inventamos el ID que normalmente daría PostgreSQL
        guardada.setPostulante(alumno);
        guardada.setMateria(materia); // Le pasamos la materia completa
        guardada.setJustificacion("Buena justificación");
        guardada.setNotaAprobacion(8.0);
        guardada.setSedePreferencia("Peergamino city vos sabes");
        guardada.setModalidadPreferencia(ModalidadTutoria.PRESENCIAL); 
        guardada.setEstado(PostulacionTutorEstado.PENDIENTE);
        
        when(postulacionTutorRepository.save(any())).thenReturn(guardada);

        var resultado = postulacionTutorService.registrarPostulacion(
            1L, 1L, 8.0, "Buena justificación", "Sede Central", ModalidadTutoria.PRESENCIAL, pdf
        );

        assertNotNull(resultado);
        assertEquals("pendiente", resultado.getStatus()); // Ahora comprobamos contra el DTO
        assertEquals("1", resultado.getId());
    }

    @Test
    void registrarPostulacion_NotaInvalida_LanzaExcepcion() {
        MockMultipartFile pdf = new MockMultipartFile("archivo", "test.pdf", "application/pdf", "data".getBytes());
        
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> {
            postulacionTutorService.registrarPostulacion(1L, 1L, 2.0, "Justificación", "Sede", ModalidadTutoria.VIRTUAL, pdf);
        });
        assertEquals("La nota de aprobación debe estar entre 4.0 y 10.0.", ex.getMessage());
    }

    @Test
    void registrarPostulacion_SedeVacia_LanzaExcepcion() {
        MockMultipartFile pdf = new MockMultipartFile("archivo", "test.pdf", "application/pdf", "data".getBytes());
        
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> {
            postulacionTutorService.registrarPostulacion(1L, 1L, 8.0, "Justificación", "", ModalidadTutoria.VIRTUAL, pdf);
        });
        assertEquals("Debe indicar una sede de preferencia.", ex.getMessage());
    }

    @Test
    void registrarPostulacion_Duplicada_LanzaExcepcion() {
        MockMultipartFile pdf = new MockMultipartFile("archivo", "test.pdf", "application/pdf", "data".getBytes());
        Alumno alumno = new Alumno(); alumno.setId(1L);
        Materia materia = new Materia(); materia.setId(1L);
        
        PostulacionTutor existente = new PostulacionTutor();
        existente.setMateria(materia);
        existente.setEstado(PostulacionTutorEstado.PENDIENTE);

        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));
        when(postulacionTutorRepository.findByPostulanteId(1L)).thenReturn(List.of(existente));

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> {
            postulacionTutorService.registrarPostulacion(1L, 1L, 8.0, "Justificación", "Sede", ModalidadTutoria.VIRTUAL, pdf);
        });
        assertEquals("Ya has postulado para esta materia y sigue pendiente.", ex.getMessage());
    }
    @Test
    void eliminarPostulacion_Exito() {
        PostulacionTutor postulacion = new PostulacionTutor();
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        postulacion.setPostulante(alumno);

        when(postulacionTutorRepository.findById(1L)).thenReturn(Optional.of(postulacion));

        postulacionTutorService.eliminarPostulacion(1L, 1L);

        // Verificamos que el repositorio efectivamente haya llamado al método delete
        verify(postulacionTutorRepository, times(1)).delete(postulacion);
    }

    @Test
    void eliminarPostulacion_NoPerteneceAlAlumno_LanzaExcepcion() {
        PostulacionTutor postulacion = new PostulacionTutor();
        Alumno alumnoDueno = new Alumno();
        alumnoDueno.setId(2L); // El dueño real es el alumno 2
        postulacion.setPostulante(alumnoDueno);

        when(postulacionTutorRepository.findById(1L)).thenReturn(Optional.of(postulacion));

        // Intenta borrarlo el alumno 1, debe saltar la excepción de seguridad
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> {
            postulacionTutorService.eliminarPostulacion(1L, 1L); 
        });

        assertEquals("No tienes permiso para eliminar esta postulación porque no te pertenece.", ex.getMessage());
        // Verificamos que jamás se haya llamado a borrar
        verify(postulacionTutorRepository, never()).delete(any());
    }

    @Test
    void obtenerPostulacionesPorAlumno_Exito() {
        PostulacionTutor postulacion = new PostulacionTutor();
        Materia materia = new Materia();
        materia.setId(1L);
        materia.setNombre("Programación");
        postulacion.setId(1L);
        postulacion.setMateria(materia);
        postulacion.setEstado(PostulacionTutorEstado.PENDIENTE);
        postulacion.setModalidadPreferencia(ModalidadTutoria.VIRTUAL);

        when(postulacionTutorRepository.findByPostulanteId(1L)).thenReturn(List.of(postulacion));

        var resultado = postulacionTutorService.obtenerPostulacionesPorAlumno(1L);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Programación", resultado.get(0).getMateria().getNombre());
    }
}