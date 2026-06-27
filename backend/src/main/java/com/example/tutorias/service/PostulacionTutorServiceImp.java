package com.example.tutorias.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tutorias.entity.Alumno;
import com.example.tutorias.entity.Materia;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.Persona;
import com.example.tutorias.entity.PostulacionTutor;
import com.example.tutorias.entity.PostulacionTutorEstado;
import com.example.tutorias.exception.ReglaNegocioException;
import com.example.tutorias.repository.AlumnoRepository;
import com.example.tutorias.repository.MateriaRepository;
import com.example.tutorias.repository.PersonaRepository;
import com.example.tutorias.repository.PostulacionTutorRepository;
import com.example.tutorias.dto.postulaciones.PostulacionTutorResponseDTO;
import java.time.LocalDateTime;

@Service
public class PostulacionTutorServiceImp implements PostulacionTutorService {
    
    @Autowired
    private AlumnoRepository alumnoRepository;
    
    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PostulacionTutorRepository postulacionTutorRepository;

    @Override
    @Transactional
    public PostulacionTutorResponseDTO registrarPostulacion(Long alumnoId, Long materiaId, Double notaAprobacion, String justificacion, String sedePreferencia, ModalidadTutoria modalidad, MultipartFile archivoPdf) {
        
        if(justificacion == null || justificacion.trim().isEmpty()) {
            throw new ReglaNegocioException("La justificación no puede estar vacía.");
        }

        if(notaAprobacion == null || notaAprobacion < 4.0 || notaAprobacion > 10.0) {
            throw new ReglaNegocioException("La nota de aprobación debe estar entre 4.0 y 10.0.");
        }

        if(sedePreferencia == null || sedePreferencia.trim().isEmpty()) {
            throw new ReglaNegocioException("Debe indicar una sede de preferencia.");
        }

        if(modalidad == null) {
            throw new ReglaNegocioException("Debe indicar una modalidad de tutoría.");
        }

        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new ReglaNegocioException("El alumno especificado no existe."));

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new ReglaNegocioException("La materia especificada no existe."));

        List<PostulacionTutor> postulacionesExistentes = postulacionTutorRepository.findByPostulanteId(alumnoId);
        boolean yaSePostulo = postulacionesExistentes.stream()
                .anyMatch(p -> p.getMateria().getId().equals(materiaId) && p.getEstado() == PostulacionTutorEstado.PENDIENTE);

        if (yaSePostulo) {
            throw new ReglaNegocioException("Ya has postulado para esta materia y sigue pendiente.");
        }

        String rutaArchivo = fileStorageService.guardarArchivo(archivoPdf);

        PostulacionTutor nuevaPostulacion = new PostulacionTutor();
        nuevaPostulacion.setPostulante(alumno);
        nuevaPostulacion.setMateria(materia);
        nuevaPostulacion.setJustificacion(justificacion);
        nuevaPostulacion.setNotaAprobacion(notaAprobacion);
        nuevaPostulacion.setPdfPath(rutaArchivo);
        nuevaPostulacion.setEstado(PostulacionTutorEstado.PENDIENTE);
        nuevaPostulacion.setSedePreferencia(sedePreferencia);
        nuevaPostulacion.setModalidadPreferencia(modalidad);

        PostulacionTutor savedPostulacion = postulacionTutorRepository.save(nuevaPostulacion);
        
        return PostulacionTutorResponseDTO.from(savedPostulacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulacionTutorResponseDTO> obtenerPostulacionesPorAlumno(Long alumnoId) {
        return postulacionTutorRepository.findByPostulanteId(alumnoId).stream()
                .map(PostulacionTutorResponseDTO::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulacionTutorResponseDTO> obtenerTodasPostulaciones() {
        return postulacionTutorRepository.findAll().stream()
                .map(PostulacionTutorResponseDTO::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String obtenerRutaPdfPostulacion(Long postulacionId) {
        PostulacionTutor postulacion = postulacionTutorRepository.findById(postulacionId)
                .orElseThrow(() -> new ReglaNegocioException("La postulación no existe."));
        return postulacion.getPdfPath();
    }

    @Override
    @Transactional
    public void actualizarEstadoPostulacion(Long postulacionId, PostulacionTutorEstado nuevoEstado, Long adminId, String adminComentario) {
        PostulacionTutor postulacion = postulacionTutorRepository.findById(postulacionId)
                .orElseThrow(() -> new ReglaNegocioException("La postulación no existe."));

        if (adminId != null) {
            Persona administrador = personaRepository.findById(adminId)
                    .orElseThrow(() -> new ReglaNegocioException("El administrador no existe."));
            postulacion.setRevisor(administrador);
        }

        postulacion.setEstado(nuevoEstado);
        postulacion.setAdminComentario(adminComentario);
        postulacion.setReviewedAt(LocalDateTime.now());
        postulacionTutorRepository.save(postulacion);
    }

    @Override
    @Transactional
    public void eliminarPostulacion(Long postulacionId, Long alumnoId) {
        PostulacionTutor postulacion = postulacionTutorRepository.findById(postulacionId)
                .orElseThrow(() -> new ReglaNegocioException("La postulación no existe."));

        if (!postulacion.getPostulante().getId().equals(alumnoId)) {
            throw new ReglaNegocioException("No tienes permiso para eliminar esta postulación porque no te pertenece.");
        }

        postulacionTutorRepository.delete(postulacion);
    }
}