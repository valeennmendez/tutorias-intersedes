package com.example.tutorias.dto.materia;

import com.example.tutorias.entity.Materia;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MateriaResponse {
    private Long id;
    private String nombre;

    public static MateriaResponse from(Materia materia) {
        return MateriaResponse.builder()
                .id(materia.getId())
                .nombre(materia.getNombre())
                .build();
    }
}
