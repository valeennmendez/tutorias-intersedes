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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TutoriaService {

    private final TutoriaRepository tutoriaRepository;
    private final TutorRepository tutorRepository;
    private final MateriaRepository materiaRepository;

    public TutoriaService(
            TutoriaRepository tutoriaRepository,
            TutorRepository tutorRepository,
            MateriaRepository materiaRepository) {
        this.tutoriaRepository = tutoriaRepository;
        this.tutorRepository = tutorRepository;
        this.materiaRepository = materiaRepository;
    }

    @Transactional
    public TutoriaResponse crearTutoria(CrearTutoriaRequest request) {
        validarHorarios(request);
        validarModalidad(request);

        Tutor tutor = tutorRepository.findById(request.getTutorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor no encontrado"));

        if (!Boolean.TRUE.equals(tutor.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tutor no esta aprobado");
        }

        Materia materia = materiaRepository.findById(request.getMateriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada"));

        boolean horarioOcupado = tutoriaRepository.existsByTutorIdAndFechaAndEstadoAndHoraInicioBeforeAndHoraFinAfter(
                tutor.getId(),
                request.getFecha(),
                EstadoTutoria.ACTIVA,
                request.getHoraFin(),
                request.getHoraInicio()
        );

        if (horarioOcupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El tutor ya tiene una tutoria en ese horario");
        }

        Tutoria tutoria = new Tutoria();
        tutoria.setNombre(request.getNombre());
        tutoria.setDescripcion(request.getDescripcion());
        tutoria.setFecha(request.getFecha());
        tutoria.setHoraInicio(request.getHoraInicio());
        tutoria.setHoraFin(request.getHoraFin());
        tutoria.setModalidad(request.getModalidad());
        tutoria.setUbicacion(request.getUbicacion());
        tutoria.setLinkVirtual(request.getLinkVirtual());
        tutoria.setCupo(request.getCupo());
        tutoria.setEstado(EstadoTutoria.ACTIVA);
        tutoria.setTutor(tutor);
        tutoria.setMateria(materia);

        Tutoria guardada = tutoriaRepository.save(tutoria);
        return toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<TutoriaResponse> obtenerTutorias() {
        return tutoriaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TutoriaResponse> obtenerTutoriasPorAlumno(Long alumnoId) {
        return tutoriaRepository.findByAlumnosId(alumnoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long obtenerCantidadInscriptos(Long tutoriaId) {
        if (!tutoriaRepository.existsById(tutoriaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutoria no encontrada");
        }

        return tutoriaRepository.countAlumnosByTutoriaId(tutoriaId);
    }

    private TutoriaResponse toResponse(Tutoria tutoria) {
        long cantidadInscriptos = tutoria.getId() == null
                ? 0
                : tutoriaRepository.countAlumnosByTutoriaId(tutoria.getId());

        return TutoriaResponse.from(tutoria, cantidadInscriptos);
    }

    private void validarHorarios(CrearTutoriaRequest request) {
        if (!request.getHoraInicio().isBefore(request.getHoraFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora de inicio debe ser anterior a la hora de fin");
        }
    }

    private void validarModalidad(CrearTutoriaRequest request) {
        ModalidadTutoria modalidad = request.getModalidad();

        if ((modalidad == ModalidadTutoria.PRESENCIAL || modalidad == ModalidadTutoria.HIBRIDA)
                && isBlank(request.getUbicacion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ubicacion es obligatoria para tutorias presenciales o hibridas");
        }

        if ((modalidad == ModalidadTutoria.VIRTUAL || modalidad == ModalidadTutoria.HIBRIDA)
                && isBlank(request.getLinkVirtual())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El link virtual es obligatorio para tutorias virtuales o hibridas");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
