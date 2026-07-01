package com.example.tutorias.dto.tutor;

import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.Sede;
import com.example.tutorias.entity.Tutoria;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class TutoriaTutorPerfilDTO {
    private Long id;
    private String nombre;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private ModalidadTutoria modalidad;
    private EstadoTutoria estado;
    private Sede sede;
    private Long materiaId;
    private String materiaNombre;

    public static TutoriaTutorPerfilDTO from(Tutoria tutoria) {
        return new TutoriaTutorPerfilDTO(
                tutoria.getId(),
                tutoria.getNombre(),
                tutoria.getFecha(),
                tutoria.getHoraInicio(),
                tutoria.getHoraFin(),
                tutoria.getModalidad(),
                tutoria.getEstado(),
                tutoria.getSede(),
                tutoria.getMateria() != null ? tutoria.getMateria().getId() : null,
                tutoria.getMateria() != null ? tutoria.getMateria().getNombre() : null
        );
    }
}
