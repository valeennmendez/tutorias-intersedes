package com.example.tutorias.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.example.tutorias.entity.Administrador;
import com.example.tutorias.entity.Alumno;
import com.example.tutorias.entity.Materia;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.PostulacionTutor;
import com.example.tutorias.entity.PostulacionTutorEstado;
import com.example.tutorias.entity.Role;
import com.example.tutorias.entity.Tutor;
import com.example.tutorias.exception.ReglaNegocioException;
import com.example.tutorias.repository.AlumnoRepository;
import com.example.tutorias.repository.MateriaRepository;
import com.example.tutorias.repository.PersonaRepository;
import com.example.tutorias.repository.PostulacionTutorRepository;
import com.example.tutorias.repository.TutorRepository;
import com.example.tutorias.util.NotificacionService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private jakarta.persistence.EntityManager entityManager; 

    @Mock
    private jakarta.persistence.Query nativeQueryMock; 

    @InjectMocks
    private PostulacionTutorServiceImp postulacionTutorService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postulacionTutorService, "entityManager", entityManager);
    }

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
    
    @Test
    void actualizarEstadoPostulacion_Aprobada_PromueveAlumno() {
        // 1. Preparamos los datos falsos
        Long idPostulacion = 1L;
        Long idAdmin = 99L;
        
        Administrador admin = new Administrador();
        admin.setId(idAdmin);

        Alumno postulante = new Alumno();
        postulante.setId(10L);
        postulante.setEmail("robertino@comunidad.unnoba.edu.ar");

        Materia materia = new Materia();
        materia.setId(5L);
        materia.setNombre("Sistemas y Organizaciones");

        PostulacionTutor postulacion = new PostulacionTutor();
        postulacion.setId(idPostulacion);
        postulacion.setPostulante(postulante);
        postulacion.setMateria(materia);
        postulacion.setEstado(PostulacionTutorEstado.PENDIENTE);

        Tutor tutorCreado = new Tutor();
        tutorCreado.setId(10L);
        tutorCreado.setMaterias(new HashSet<>()); // Lista vacía para que no tire NullPointer
        
        // 2. Simulamos el comportamiento de los repositorios
        when(postulacionTutorRepository.findById(idPostulacion)).thenReturn(Optional.of(postulacion));
        when(personaRepository.findById(idAdmin)).thenReturn(Optional.of(admin));
        
        // Simula que NO es tutor la primera vez que pregunta
        when(tutorRepository.findById(10L))
            .thenReturn(Optional.empty()) // Para el if (!yaEsTutor)
            .thenReturn(Optional.of(tutorCreado)); // Para recuperarlo después de las queries nativas

        // 3. Simulamos la magia oscura del EntityManager (Queries Nativas)
        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQueryMock);
        when(nativeQueryMock.setParameter(anyString(), any())).thenReturn(nativeQueryMock);
        when(nativeQueryMock.executeUpdate()).thenReturn(1);
        
        when(entityManager.merge(any(Materia.class))).thenReturn(materia);

        // 4. Ejecutamos el método real
        postulacionTutorService.actualizarEstadoPostulacion(idPostulacion, PostulacionTutorEstado.APROBADA, idAdmin, "Todo OK");

        // 5. Verificamos que todo haya ocurrido como esperábamos
        assertEquals(PostulacionTutorEstado.APROBADA, postulacion.getEstado());
        assertEquals(Role.TUTOR, postulante.getRole());
        
        // Verificamos que se hayan ejecutado los updates nativos (2 veces: el INSERT y el UPDATE)
        verify(entityManager, times(2)).createNativeQuery(anyString());
        
        // Verificamos que se guardó el tutor con la materia asignada
        verify(tutorRepository, times(1)).save(tutorCreado);
        assertTrue(tutorCreado.getMaterias().contains(materia));

        // Verificamos que se mandó el mail de éxito
        verify(notificacionService, times(1)).enviarNotificacionAprobacion(postulante.getEmail(), materia.getNombre());
    }

    @Test
    void actualizarEstadoPostulacion_Rechazada_EnviaCorreoConMotivo() {
        Long idPostulacion = 1L;
        Long idAdmin = 99L;
        
        Administrador admin = new Administrador();
        admin.setId(idAdmin);

        Alumno postulante = new Alumno();
        postulante.setId(10L);
        postulante.setEmail("robertino@comunidad.unnoba.edu.ar");

        Materia materia = new Materia();
        materia.setNombre("Sistemas y Organizaciones");

        PostulacionTutor postulacion = new PostulacionTutor();
        postulacion.setId(idPostulacion);
        postulacion.setPostulante(postulante);
        postulacion.setMateria(materia);
        postulacion.setEstado(PostulacionTutorEstado.PENDIENTE);

        when(postulacionTutorRepository.findById(idPostulacion)).thenReturn(Optional.of(postulacion));
        when(personaRepository.findById(idAdmin)).thenReturn(Optional.of(admin));

        // Ejecutamos el rechazo
        String motivoRechazo = "Falta certificado de alumno regular.";
        postulacionTutorService.actualizarEstadoPostulacion(idPostulacion, PostulacionTutorEstado.RECHAZADA, idAdmin, motivoRechazo);

        // Verificamos que cambió el estado a rechazada
        assertEquals(PostulacionTutorEstado.RECHAZADA, postulacion.getEstado());
        assertEquals(motivoRechazo, postulacion.getAdminComentario());

        // Verificamos que NUNCA se intentó guardar un tutor
        verify(tutorRepository, never()).save(any());
        verify(entityManager, never()).createNativeQuery(anyString());

        // Verificamos que se mandó el mail de rechazo
        verify(notificacionService, times(1)).enviarNotificacionRechazo(postulante.getEmail(), materia.getNombre(), motivoRechazo);
    }
    @Test
    void actualizarEstadoPostulacion_PostulacionNoExiste_LanzaExcepcion() {
        when(postulacionTutorRepository.findById(99L)).thenReturn(Optional.empty());

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> {
            postulacionTutorService.actualizarEstadoPostulacion(99L, PostulacionTutorEstado.APROBADA, 1L, "Comentario");
        });

        assertEquals("La postulación no existe.", ex.getMessage());
        verify(personaRepository, never()).findById(any()); // Verifica que el proceso se cortó ahí
    }

    @Test
    void actualizarEstadoPostulacion_AlumnoYaEsTutor_AgregaMateriaSinConsultasNativas() {
        Long idPostulacion = 1L;
        Long idAdmin = 99L;
        
        Administrador admin = new Administrador(); admin.setId(idAdmin);
        Alumno postulante = new Alumno(); postulante.setId(10L); postulante.setEmail("test@test.com");
        Materia nuevaMateria = new Materia(); nuevaMateria.setId(5L); nuevaMateria.setNombre("Programación");

        PostulacionTutor postulacion = new PostulacionTutor();
        postulacion.setId(idPostulacion);
        postulacion.setPostulante(postulante);
        postulacion.setMateria(nuevaMateria);
        postulacion.setEstado(PostulacionTutorEstado.PENDIENTE);

        // Acá simulamos que el tutor YA EXISTE en la base de datos
        Tutor tutorExistente = new Tutor();
        tutorExistente.setId(10L);
        tutorExistente.setMaterias(new HashSet<>()); 

        when(postulacionTutorRepository.findById(idPostulacion)).thenReturn(Optional.of(postulacion));
        when(personaRepository.findById(idAdmin)).thenReturn(Optional.of(admin));
        
        // Cuando pregunte si es tutor, le decimos que SÍ (Optional.of)
        when(tutorRepository.findById(10L)).thenReturn(Optional.of(tutorExistente));
        
        when(entityManager.merge(any(Materia.class))).thenReturn(nuevaMateria);

        // Ejecutamos
        postulacionTutorService.actualizarEstadoPostulacion(idPostulacion, PostulacionTutorEstado.APROBADA, idAdmin, "OK");

        // Verificamos que NUNCA llamó a las queries nativas
        verify(entityManager, never()).createNativeQuery(anyString());
        
        // Pero SÍ guardó la materia nueva en el tutor que ya existía
        verify(tutorRepository, times(1)).save(tutorExistente);
        assertTrue(tutorExistente.getMaterias().contains(nuevaMateria));
    }

    @Test
    void actualizarEstadoPostulacion_ErrorAlRecuperarTutor_LanzaExcepcion() {
        Administrador admin = new Administrador(); admin.setId(99L);
        Alumno postulante = new Alumno(); postulante.setId(10L);
        Materia materia = new Materia(); materia.setId(5L);

        PostulacionTutor postulacion = new PostulacionTutor();
        postulacion.setId(1L);
        postulacion.setPostulante(postulante);
        postulacion.setMateria(materia);
        postulacion.setEstado(PostulacionTutorEstado.PENDIENTE);

        when(postulacionTutorRepository.findById(1L)).thenReturn(Optional.of(postulacion));
        when(personaRepository.findById(99L)).thenReturn(Optional.of(admin));
        
        // Simula el proceso: 1. No existe (if). 2. Queries nativas. 3. Sigue sin existir al recuperarlo.
        when(tutorRepository.findById(10L)).thenReturn(Optional.empty()); // Se devuelve empty las dos veces

        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQueryMock);
        when(nativeQueryMock.setParameter(anyString(), any())).thenReturn(nativeQueryMock);
        when(nativeQueryMock.executeUpdate()).thenReturn(1);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> {
            postulacionTutorService.actualizarEstadoPostulacion(1L, PostulacionTutorEstado.APROBADA, 99L, "OK");
        });

        assertEquals("Error al crear el tutor.", ex.getMessage());
    }
}