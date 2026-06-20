package com.example.tutorias.dto.avisos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CrearAvisoRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;

    @NotNull(message = "El ID de la tutoría es obligatorio")
    private Long tutoriaId;
}