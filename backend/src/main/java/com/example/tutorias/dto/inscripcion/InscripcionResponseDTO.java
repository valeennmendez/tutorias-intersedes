package com.example.tutorias.dto.inscripcion;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
//esto sería lo que le devuelvo al front cuando le pido las inscripciones de un alumno, para que pueda ver a qué tutorías se ha inscrito y su estado
public class InscripcionResponseDTO {
    private Long id;
    private Long tutoriaId;
    private String nombreTutoria;
    private String nombreTutor;
    private String nombreAlumno;
    private String emailAlumno;
    private String status;
    private LocalDateTime fechaInscripcion;
}