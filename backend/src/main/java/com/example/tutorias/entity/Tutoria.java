package com.example.tutorias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
   
   
    @Column(name = "link_drive")
    private String linkDrive;

    @Enumerated(EnumType.STRING)
    private ModalidadTutoria modalidad;

    @Enumerated(EnumType.STRING)
    private EstadoTutoria estado = EstadoTutoria.ACTIVA;
    
    @Enumerated(EnumType.STRING)
    private Sede sede;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @Column(name = "tutor_id", insertable = false, updatable = false)
    private Long tutorId;

    @ManyToOne
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @OneToMany(mappedBy = "tutoria")
    private List<Inscripcion> inscripciones;
}
