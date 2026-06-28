package com.example.tutorias.dto.feedback;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearFeedbackRequestDTO {
    private Integer calificacion;
    private String comentarios;
    private boolean esAnonimo;
}