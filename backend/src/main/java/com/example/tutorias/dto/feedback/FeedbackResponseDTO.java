package com.example.tutorias.dto.feedback;

import com.example.tutorias.entity.Feedback;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FeedbackResponseDTO {
    private Long id;
    private Long inscripcionId;
    private String nombreTutoria;
    private String nombreAlumno;
    private Integer calificacion;
    private String comentarios;
    private LocalDateTime fecha;

    // Método estático para mapear limpiamente
    public static FeedbackResponseDTO from(Feedback feedback) {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setId(feedback.getId());
        dto.setInscripcionId(feedback.getInscripcion().getId());
        dto.setNombreTutoria(feedback.getInscripcion().getTutoria().getNombre());
        dto.setCalificacion(feedback.getCalificacion());
        dto.setComentarios(feedback.getComentarios());
        dto.setFecha(feedback.getCreatedAt());

        // LÓGICA DE ANONIMATO ESTRICTA EN EL BACKEND
        if (Boolean.TRUE.equals(feedback.isEsAnonimo())) {
            dto.setNombreAlumno("Anónimo");
        } else {
            dto.setNombreAlumno(feedback.getInscripcion().getAlumno().getNombre() + " " + 
                                feedback.getInscripcion().getAlumno().getApellido());
        }

        return dto;
    }
}