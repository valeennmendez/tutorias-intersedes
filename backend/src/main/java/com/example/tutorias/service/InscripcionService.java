package com.example.tutorias.service;

import com.example.tutorias.dto.inscripcion.HistorialTutoriaAlumnoDTO;
import com.example.tutorias.dto.inscripcion.InscripcionResponseDTO;
import com.example.tutorias.entity.*;
import com.example.tutorias.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final TutoriaRepository tutoriaRepository;
    private final AlumnoRepository alumnoRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository, TutoriaRepository tutoriaRepository, AlumnoRepository alumnoRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.tutoriaRepository = tutoriaRepository;
        this.alumnoRepository = alumnoRepository;
    }

    @Transactional
    public InscripcionResponseDTO inscribirAlumno(Long tutoriaId, String emailAlumno) {
        //Validar Feedback Pendiente (RF-08)
        if (inscripcionRepository.tieneFeedbackPendiente(emailAlumno)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No podés inscribirte: tenés encuestas de feedback pendientes.");
        }

        Tutoria tutoria = tutoriaRepository.findById(tutoriaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutoría no encontrada."));

        if (tutoria.getEstado() != EstadoTutoria.ACTIVA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La tutoría no se encuentra activa.");
        }

        Alumno alumno = alumnoRepository.findByEmail(emailAlumno)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado."));

        // 2. Validar si ya existe una inscripción previa
        Optional<Inscripcion> inscripcionPrevia = inscripcionRepository.findByTutoriaIdAndAlumnoEmail(tutoriaId, emailAlumno);
        
        if (inscripcionPrevia.isPresent()) {
            Inscripcion inscripcion = inscripcionPrevia.get();
            if (inscripcion.getStatus() == InscripcionStatus.ACTIVA) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya estás inscripto en esta tutoría.");
            } else {
                // Si estaba cancelada, verificamos cupo y la reactivamos
                validarCupos(tutoria);
                inscripcion.setStatus(InscripcionStatus.ACTIVA);
                return toResponse(inscripcionRepository.save(inscripcion));
            }
        }

        // 3. Validar Cupos para una inscripción nueva
        validarCupos(tutoria);

        // 4. Guardar nueva Inscripción
        Inscripcion nuevaInscripcion = new Inscripcion();
        nuevaInscripcion.setTutoria(tutoria);
        nuevaInscripcion.setAlumno(alumno);
        nuevaInscripcion.setStatus(InscripcionStatus.ACTIVA);

        return toResponse(inscripcionRepository.save(nuevaInscripcion));
    }

    @Transactional
    public void cancelarInscripcion(Long tutoriaId, String emailAlumno) {
        Inscripcion inscripcion = inscripcionRepository.findByTutoriaIdAndAlumnoEmail(tutoriaId, emailAlumno)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No tenés una inscripción en esta tutoría."));
        
        if (inscripcion.getStatus() == InscripcionStatus.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La inscripción ya estaba cancelada.");
        }

        // Baja lógica
        inscripcion.setStatus(InscripcionStatus.CANCELADA);
        inscripcionRepository.save(inscripcion);
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> obtenerInscripcionesPorTutoria(Long tutoriaId, String emailTutor) {
        Tutoria tutoria = tutoriaRepository.findById(tutoriaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutoría no encontrada."));

        // VALIDACIÓN DE SEGURIDAD: ¿El tutor logueado es el dueño de esta tutoría?
        if (!tutoria.getTutor().getEmail().equals(emailTutor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para ver los inscriptos de esta tutoría.");
        }

        return inscripcionRepository.findByTutoriaIdAndStatus(tutoriaId, InscripcionStatus.ACTIVA).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> obtenerMisInscripciones(String emailAlumno) {
        return inscripcionRepository.findByAlumnoEmail(emailAlumno).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HistorialTutoriaAlumnoDTO> obtenerHistorialTutoriasAlumno(String emailAlumno) {
        return inscripcionRepository.findByAlumnoEmailAndStatus(emailAlumno, InscripcionStatus.ACTIVA).stream()
                .filter(inscripcion -> inscripcion.getTutoria().getEstado() != EstadoTutoria.CANCELADA)
                .filter(inscripcion -> tutoriaFinalizada(inscripcion.getTutoria()))
                .map(this::toHistorialResponse)
                .toList();
    }


    private void validarCupos(Tutoria tutoria) {
        long inscriptosActuales = inscripcionRepository.countByTutoriaIdAndStatus(tutoria.getId(), InscripcionStatus.ACTIVA);
        if (inscriptosActuales >= tutoria.getCupo()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay más cupos disponibles para esta tutoría.");
        }
    }


    private InscripcionResponseDTO toResponse(Inscripcion inscripcion) {
        InscripcionResponseDTO dto = new InscripcionResponseDTO();
        dto.setId(inscripcion.getId());
        dto.setTutoriaId(inscripcion.getTutoria().getId());
        dto.setNombreTutoria(inscripcion.getTutoria().getNombre());
        dto.setNombreTutor(inscripcion.getTutoria().getTutor().getNombre() + " " + inscripcion.getTutoria().getTutor().getApellido());
        dto.setStatus(inscripcion.getStatus().name());
        dto.setFechaInscripcion(inscripcion.getCreatedAt());
        dto.setNombreAlumno(inscripcion.getAlumno().getNombre());
        dto.setNombreAlumno(inscripcion.getAlumno().getNombre() + " " + inscripcion.getAlumno().getApellido());
        dto.setEmailAlumno(inscripcion.getAlumno().getEmail());
        return dto;
    }

    private HistorialTutoriaAlumnoDTO toHistorialResponse(Inscripcion inscripcion) {
        Tutoria tutoria = inscripcion.getTutoria();
        HistorialTutoriaAlumnoDTO dto = new HistorialTutoriaAlumnoDTO();
        dto.setInscripcionId(inscripcion.getId());
        dto.setTutoriaId(tutoria.getId());
        dto.setNombreTutoria(tutoria.getNombre());
        dto.setMateriaNombre(tutoria.getMateria() != null ? tutoria.getMateria().getNombre() : null);
        dto.setNombreTutor(tutoria.getTutor().getNombre() + " " + tutoria.getTutor().getApellido());
        dto.setFecha(tutoria.getFecha());
        dto.setHoraInicio(tutoria.getHoraInicio());
        dto.setHoraFin(tutoria.getHoraFin());
        dto.setModalidad(tutoria.getModalidad());
        dto.setSede(tutoria.getSede());
        return dto;
    }

    private boolean tutoriaFinalizada(Tutoria tutoria) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        return tutoria.getFecha().isBefore(hoy)
                || (tutoria.getFecha().isEqual(hoy) && !tutoria.getHoraFin().isAfter(ahora));
    }
}
