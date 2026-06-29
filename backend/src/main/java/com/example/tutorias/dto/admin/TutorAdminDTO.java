package com.example.tutorias.dto.admin;

import java.util.List;
import java.util.stream.Collectors;
import com.example.tutorias.entity.Tutor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorAdminDTO {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String cuit;
    private String titulo;
    private List<String> materias;

    public static TutorAdminDTO from(Tutor tutor) {
        TutorAdminDTO dto = new TutorAdminDTO();
        dto.setId(tutor.getId());
        dto.setNombreCompleto(tutor.getNombre() + " " + tutor.getApellido());
        dto.setEmail(tutor.getEmail());
        dto.setCuit(tutor.getCuit());
        dto.setTitulo(tutor.getTitulo());
        if (tutor.getMaterias() != null) {
            dto.setMaterias(tutor.getMaterias().stream()
                    .map(materia -> materia.getNombre())
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}