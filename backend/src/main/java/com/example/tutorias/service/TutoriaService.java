package com.example.tutorias.service;

import com.example.tutorias.dto.tutoria.CrearTutoriaRequest;
import com.example.tutorias.dto.tutoria.TutoriaResponse;
import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.InscripcionStatus;
import com.example.tutorias.entity.Materia;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.Persona;
import com.example.tutorias.entity.Sede;
import com.example.tutorias.entity.Tutor;
import com.example.tutorias.entity.Tutoria;
import com.example.tutorias.exception.ReglaNegocioException;
import com.example.tutorias.repository.InscripcionRepository;
import com.example.tutorias.repository.MateriaRepository;
import com.example.tutorias.repository.PersonaRepository;
import com.example.tutorias.repository.TutorRepository;
import com.example.tutorias.repository.TutoriaRepository;
import com.example.tutorias.specification.TutoriaSpecification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class TutoriaService {

    private final TutoriaRepository tutoriaRepository;
    private final TutorRepository tutorRepository;
    private final MateriaRepository materiaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final PersonaRepository personaRepository;

    public TutoriaService(
            TutoriaRepository tutoriaRepository,
            TutorRepository tutorRepository,
            MateriaRepository materiaRepository,
            InscripcionRepository inscripcionRepository,
            PersonaRepository personaRepository) {
        this.tutoriaRepository = tutoriaRepository;
        this.tutorRepository = tutorRepository;
        this.materiaRepository = materiaRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.personaRepository = personaRepository;
    }

    @Transactional
    public TutoriaResponse crearTutoria(CrearTutoriaRequest request) {
        validarHorarios(request);
        validarModalidad(request);

        Tutor tutor = tutorRepository.findById(request.getTutorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor no encontrado"));

        if (!Boolean.TRUE.equals(tutor.getEstadoTutor())) { //accedemos a la nueva columna
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

        if (request.getCupo() <= 0 || request.getCupo() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cupo debe ser un valor entre 1 y 50");
        }

        if (horarioOcupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El tutor ya tiene una tutoria en ese horario");
        }

        if(request.getSede() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sede es obligatoria");
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
        tutoria.setLinkDrive(request.getLinkDrive());
        tutoria.setCupo(request.getCupo());
        tutoria.setEstado(EstadoTutoria.ACTIVA);
        tutoria.setTutor(tutor);
        tutoria.setMateria(materia);
        tutoria.setSede(request.getSede());
        Tutoria guardada = tutoriaRepository.save(tutoria);
        return toResponse(guardada);
    }

    @Transactional
    public void eliminarTutoria(Long tutoriaId, String emailTutor) {
        Tutoria tutoria = tutoriaRepository.findById(tutoriaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutoria no encontrada"));

        Tutor tutor = tutorRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor no encontrado"));

        if (tutoria.getTutor() == null || !tutoria.getTutor().getId().equals(tutor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar esta tutoria");
        }

        if (tutoria.getEstado() == EstadoTutoria.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La tutoria ya esta dada de baja");
        }

        if (tutoriaYaInicio(tutoria)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede dar de baja una tutoria que ya inicio");
        }

        tutoria.setEstado(EstadoTutoria.CANCELADA);
        tutoriaRepository.save(tutoria);
    }

    @Transactional(readOnly = true)
    public List<TutoriaResponse> obtenerTutorias() {
        return tutoriaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    //ahora para traer las tutorías de lalumno, la relación es a través de la entidad Inscripcion, que tiene un estado, y solo queremos traer las tutorías activas
    @Transactional(readOnly = true)
    public List<TutoriaResponse> obtenerTutoriasPorAlumno(Long alumnoId) {
        return tutoriaRepository.findByInscripciones_Alumno_IdAndInscripciones_Status(alumnoId, InscripcionStatus.ACTIVA).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long obtenerCantidadInscriptos(Long tutoriaId) {
        if (!tutoriaRepository.existsById(tutoriaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutoria no encontrada");
        }
        //traemos aquella cant de alumnos que tienen inscripciones activas para la tutoria
        return inscripcionRepository.countByTutoriaIdAndStatus(tutoriaId, InscripcionStatus.ACTIVA);
    }

    private TutoriaResponse toResponse(Tutoria tutoria) {
        long cantidadInscriptos = tutoria.getId() == null
                ? 0
                : inscripcionRepository.countByTutoriaIdAndStatus(tutoria.getId(), InscripcionStatus.ACTIVA);

        String tutorNombre = null;
        Long tutorId = null;
        if (tutoria.getTutor() != null) {
            Tutor tutor = tutoria.getTutor();
            tutorId = tutor.getId();
            tutorNombre = tutor.getNombre() + " " + tutor.getApellido();
        } else if (tutoria.getTutorId() != null) {
            tutorId = tutoria.getTutorId();
            tutorNombre = personaRepository.findById(tutoria.getTutorId())
                    .map(p -> p.getNombre() + " " + p.getApellido())
                    .orElse(null);
        }

        return TutoriaResponse.builder()
                .id(tutoria.getId())
                .nombre(tutoria.getNombre())
                .descripcion(tutoria.getDescripcion())
                .fecha(tutoria.getFecha())
                .horaInicio(tutoria.getHoraInicio())
                .horaFin(tutoria.getHoraFin())
                .cupo(tutoria.getCupo())
                .ubicacion(tutoria.getUbicacion())
                .linkVirtual(tutoria.getLinkVirtual())
                .linkDrive(tutoria.getLinkDrive())
                .modalidad(tutoria.getModalidad())
                .estado(tutoria.getEstado())
                .tutorId(tutorId)
                .tutorNombre(tutorNombre)
                .materiaId(tutoria.getMateria() != null ? tutoria.getMateria().getId() : null)
                .materiaNombre(tutoria.getMateria() != null ? tutoria.getMateria().getNombre() : null)
                .cantidadInscriptos(cantidadInscriptos)
                .sede(tutoria.getSede() != null ? tutoria.getSede() : null)
                .build();
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

    private boolean tutoriaYaInicio(Tutoria tutoria) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        return tutoria.getFecha().isBefore(hoy)
                || (tutoria.getFecha().isEqual(hoy) && !tutoria.getHoraInicio().isAfter(ahora));
    }

    @Transactional(readOnly = true)
    public TutoriaResponse obtenerTutoriaPorId(Long id) {
        Tutoria tutoria = tutoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutoría no encontrada"));
        
        return toResponse(tutoria); 
    }

    @Transactional(readOnly = true)
    public List<TutoriaResponse> buscarTutoriasConFiltros(String materia, Sede sede, ModalidadTutoria modalidad) {
        
        Specification<Tutoria> spec = TutoriaSpecification.conFiltros(materia, sede, modalidad);
        
        List<Tutoria> tutoriasEncontradas = tutoriaRepository.findAll(spec);
        
        // Si no se encuentran resultados, Trello dice que "el sistema deberá informar al usuario"
        if (tutoriasEncontradas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron tutorías con los filtros aplicados.");
        }

        return tutoriasEncontradas.stream()
                .map(this::toResponse)
                .toList();
    }

    private TutoriaResponse guardarLink(Long tutoriaId, String link) {
        Tutoria tutoria = tutoriaRepository.findById(tutoriaId)
                .orElseThrow(() -> new ReglaNegocioException("Tutoria no encontrada con ID: " + tutoriaId));

        tutoria.setLinkDrive(link);

        Tutoria tutoriaGuardada = tutoriaRepository.save(tutoria);

        return TutoriaResponse.from(
                tutoriaGuardada,
                tutoriaGuardada.getInscripciones() != null
                        ? tutoriaGuardada.getInscripciones().size()
                        : 0
        );
}

    public TutoriaResponse agregarLink(Long tutoriaId, String link) {
        return guardarLink(tutoriaId, link);
    }

    public TutoriaResponse actualizarLink(Long tutoriaId, String link) {
        return guardarLink(tutoriaId, link);
    }

    public TutoriaResponse eliminarLink(Long tutoriaId) {
        return guardarLink(tutoriaId, null);
    }
}


