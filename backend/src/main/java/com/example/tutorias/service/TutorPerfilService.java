package com.example.tutorias.service;

import com.example.tutorias.dto.feedback.FeedbackResponseDTO;
import com.example.tutorias.dto.tutor.MateriaTutorPerfilDTO;
import com.example.tutorias.dto.tutor.PerfilTutorResponseDTO;
import com.example.tutorias.dto.tutor.TutoriaTutorPerfilDTO;
import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.Tutor;
import com.example.tutorias.entity.Tutoria;
import com.example.tutorias.repository.FeedbackRepository;
import com.example.tutorias.repository.TutorRepository;
import com.example.tutorias.repository.TutoriaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class TutorPerfilService {

    private static final int CANTIDAD_RESENAS_RECIENTES = 5;

    private final TutorRepository tutorRepository;
    private final TutoriaRepository tutoriaRepository;
    private final FeedbackRepository feedbackRepository;

    public TutorPerfilService(
            TutorRepository tutorRepository,
            TutoriaRepository tutoriaRepository,
            FeedbackRepository feedbackRepository) {
        this.tutorRepository = tutorRepository;
        this.tutoriaRepository = tutoriaRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional(readOnly = true)
    public PerfilTutorResponseDTO obtenerPerfil(Long tutorId) {
        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor no encontrado"));

        if (!Boolean.TRUE.equals(tutor.getEstadoTutor())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor no encontrado");
        }

        List<Tutoria> tutoriasActivas = tutoriaRepository
                .findByTutorIdAndEstadoOrderByFechaAscHoraInicioAsc(tutorId, EstadoTutoria.ACTIVA);

        Double promedio = feedbackRepository.obtenerPromedioEstrellasPorTutor(tutorId);
        Long cantidadResenas = feedbackRepository.countByInscripcion_Tutoria_Tutor_Id(tutorId);
        List<FeedbackResponseDTO> resenasRecientes = feedbackRepository
                .findByInscripcion_Tutoria_Tutor_Id(
                        tutorId,
                        PageRequest.of(0, CANTIDAD_RESENAS_RECIENTES, Sort.by(Sort.Direction.DESC, "createdAt"))
                )
                .map(FeedbackResponseDTO::from)
                .getContent();

        return PerfilTutorResponseDTO.builder()
                .tutorId(tutor.getId())
                .nombreCompleto(nombreCompleto(tutor))
                .email(tutor.getEmail())
                .titulo(tutor.getTitulo())
                .carrera(tutor.getCarrera() != null ? tutor.getCarrera().getNombre() : null)
                .promedioCalificacion(promedio != null ? promedio : 0.0)
                .cantidadResenas(cantidadResenas)
                .cantidadTutoriasActivas(tutoriasActivas.size())
                .materias(tutor.getMaterias().stream()
                        .sorted(Comparator.comparing(materia -> materia.getNombre().toLowerCase()))
                        .map(MateriaTutorPerfilDTO::from)
                        .toList())
                .tutoriasActivas(tutoriasActivas.stream()
                        .map(TutoriaTutorPerfilDTO::from)
                        .toList())
                .resenasRecientes(resenasRecientes)
                .build();
    }

    private String nombreCompleto(Tutor tutor) {
        String nombre = tutor.getNombre() != null ? tutor.getNombre() : "";
        String apellido = tutor.getApellido() != null ? tutor.getApellido() : "";
        return (nombre + " " + apellido).trim();
    }
}
