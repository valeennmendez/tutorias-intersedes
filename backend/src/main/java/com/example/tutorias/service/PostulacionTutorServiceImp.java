package com.example.tutorias.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.tutorias.entity.Alumno;
import com.example.tutorias.entity.Materia;
import com.example.tutorias.entity.PostulacionTutor;
import com.example.tutorias.entity.PostulacionTutorEstado;
import com.example.tutorias.exception.ReglaNegocioException;
import com.example.tutorias.repository.AlumnoRepository;
import com.example.tutorias.repository.MateriaRepository;
import com.example.tutorias.repository.PostulacionTutorRepository;

import jakarta.transaction.Transactional;

@Service
public class PostulacionTutorServiceImp implements PostulacionTutorService {
    
    @Autowired
    private AlumnoRepository alumnoRepository;
    
    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PostulacionTutorRepository postulacionTutorRepository;

    @Override
    public PostulacionTutor registrarPostulacion(Long alumnoId, Long materiaId, Double notaAprobacion, String justificacion, MultipartFile archivoPdf) {
        //validaciones que se deben cumplir antes de registrar la postulacion
        if(justificacion == null || justificacion.trim().isEmpty()) {
            throw new ReglaNegocioException("La justificación no puede estar vacía.");
        }

        if(notaAprobacion == null || notaAprobacion < 4.0 || notaAprobacion > 10.0) {
            throw new ReglaNegocioException("La nota de aprobación debe estar entre 4.0 y 10.0.");
        }

        // 2. Verificar existencia de Alumno y Materia
        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new ReglaNegocioException("El alumno especificado no existe."));

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new ReglaNegocioException("La materia especificada no existe."));

        //No duplicar postulaciones para la misma materia por el mismo alumno
        List<PostulacionTutor> postulacionesExistentes = postulacionTutorRepository.findByPostulanteId(alumnoId);
        boolean yaSePostulo = postulacionesExistentes.stream()
                .anyMatch(p -> p.getMateria().getId().equals(materiaId) && p.getEstado() == PostulacionTutorEstado.PENDIENTE);

        if (yaSePostulo) {
            throw new ReglaNegocioException("Ya has postulado para esta materia.");
        }

        String rutaArchivo = fileStorageService.guardarArchivo(archivoPdf);

        PostulacionTutor nuevaPostulacion = new PostulacionTutor();
        nuevaPostulacion.setPostulante(alumno);
        nuevaPostulacion.setMateria(materia);
        nuevaPostulacion.setJustificacion(justificacion);
        nuevaPostulacion.setNotaAprobacion(notaAprobacion);
        nuevaPostulacion.setPdfPath(rutaArchivo);
        nuevaPostulacion.setEstado(PostulacionTutorEstado.PENDIENTE);


        return postulacionTutorRepository.save(nuevaPostulacion); // Retornar la postulación registrada
    }
    @Override
    @Transactional
    public List<PostulacionTutor> obtenerPostulacionesPorAlumno(Long alumnoId) {
        return postulacionTutorRepository.findByPostulanteId(alumnoId);
    }

    @Override
    @Transactional
    public void actualizarEstadoPostulacion(Long postulacionId, PostulacionTutorEstado nuevoEstado) {
        PostulacionTutor postulacion = postulacionTutorRepository.findById(postulacionId)
                .orElseThrow(() -> new ReglaNegocioException("La postulación no existe."));

        postulacion.setEstado(nuevoEstado);
        postulacionTutorRepository.save(postulacion);
    }

    @Override
    @Transactional
    public void eliminarPostulacion(Long postulacionId, Long alumnoId) {
        PostulacionTutor postulacion = postulacionTutorRepository.findById(postulacionId)
                .orElseThrow(() -> new ReglaNegocioException("La postulación no existe."));

        // LA VALIDACIÓN DE SEGURIDAD
        if (!postulacion.getPostulante().getId().equals(alumnoId)) {
            // Si el ID del dueño de la postulación no coincide con el del usuario logueado no lo dejamos eliminar
            throw new ReglaNegocioException("No tienes permiso para eliminar esta postulación porque no te pertenece.");
        }

        postulacionTutorRepository.delete(postulacion);
    }
}
