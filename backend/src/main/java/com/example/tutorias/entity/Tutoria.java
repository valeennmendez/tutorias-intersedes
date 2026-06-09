package com.example.tutorias.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Tutoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer cupo;
    private String ubicacion;
    private String linkVirtual;

    @Enumerated(EnumType.STRING)
    private ModalidadTutoria modalidad;

    @Enumerated(EnumType.STRING)
    private EstadoTutoria estado = EstadoTutoria.ACTIVA;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @ManyToOne
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @ManyToMany
    @JoinTable(
        name = "tutoria_alumno",
        joinColumns = @JoinColumn(name = "tutoria_id"),
        inverseJoinColumns = @JoinColumn(name = "alumno_id")
    )
    private List<Alumno> alumnos;
}
