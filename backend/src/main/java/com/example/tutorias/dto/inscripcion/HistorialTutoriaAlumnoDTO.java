package com.example.tutorias.dto.inscripcion;

import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.Sede;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class HistorialTutoriaAlumnoDTO {
    private Long inscripcionId;
    private Long tutoriaId;
    private String nombreTutoria;
    private String materiaNombre;
    private String nombreTutor;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private ModalidadTutoria modalidad;
    private Sede sede;
}
