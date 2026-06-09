package com.example.tutorias.dto.tutoria;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CantidadInscriptosResponse {
    private Long tutoriaId;
    private long cantidadInscriptos;
}
