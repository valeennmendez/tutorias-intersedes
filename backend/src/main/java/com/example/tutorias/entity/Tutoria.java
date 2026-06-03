package com.example.tutorias.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;



@Getter
@Setter
@Entity
public class Tutoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
     private String nombre;
    private Integer cupo;

    @ManyToOne
    @JoinColumn(name = "tutor_id") // Creada por un Tutor
    private Tutor tutor;

    @ManyToOne
    @JoinColumn(name = "materia_id") // Pertenece a una Materia
    private Materia materia;

    // Alumnos inscritos en la tutoría (Genera la tabla intermedia tutoria_alumno)
    @ManyToMany
    @JoinTable(
        name = "tutoria_alumno",
        joinColumns = @JoinColumn(name = "tutoria_id"),
        inverseJoinColumns = @JoinColumn(name = "alumno_id")
    )
    private List<Alumno> alumnos;
}