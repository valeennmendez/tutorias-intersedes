package com.example.tutorias.dto.tutor;

import com.example.tutorias.dto.feedback.FeedbackResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PerfilTutorResponseDTO {
    private Long tutorId;
    private String nombreCompleto;
    private String email;
    private String titulo;
    private String carrera;
    private Double promedioCalificacion;
    private Long cantidadResenas;
    private Integer cantidadTutoriasActivas;
    private List<MateriaTutorPerfilDTO> materias;
    private List<TutoriaTutorPerfilDTO> tutoriasActivas;
    private List<FeedbackResponseDTO> resenasRecientes;
}
