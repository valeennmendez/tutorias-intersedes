package com.example.tutorias.dto.avisos;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvisoResponseDTO {
    
    private Long id;
    private String titulo;
    private String contenido;
    private LocalDateTime fechaCreacion;
    
    // Datos extra que le salvan la vida al Frontend para no tener que hacer más peticiones
    private Long tutoriaId;
    private String nombreTutoria; 
    private String nombreTutor;
}