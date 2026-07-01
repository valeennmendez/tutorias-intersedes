package com.example.tutorias.dto.tutor;

import com.example.tutorias.entity.Materia;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MateriaTutorPerfilDTO {
    private Long id;
    private String nombre;

    public static MateriaTutorPerfilDTO from(Materia materia) {
        return new MateriaTutorPerfilDTO(materia.getId(), materia.getNombre());
    }
}
