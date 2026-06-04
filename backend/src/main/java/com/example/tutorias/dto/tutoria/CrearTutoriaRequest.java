package com.example.tutorias.dto.tutoria;

import com.example.tutorias.entity.ModalidadTutoria;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CrearTutoriaRequest {

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    private Long tutorId;

    @NotNull
    private Long materiaId;

    @NotNull
    @FutureOrPresent
    private LocalDate fecha;

    @NotNull
    private LocalTime horaInicio;

    @NotNull
    private LocalTime horaFin;

    @NotNull
    private ModalidadTutoria modalidad;

    private String ubicacion;
    private String linkVirtual;

    @NotNull
    @Positive
    private Integer cupo;
}
