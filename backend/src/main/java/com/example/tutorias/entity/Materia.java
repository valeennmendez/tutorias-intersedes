package com.example.tutorias.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter

@Entity
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    // Una materia puede tener asignadas muchas tutorías
    @OneToMany(mappedBy = "materia")
    private List<Tutoria> tutorias;

    @ManyToMany(mappedBy = "materias")
    private Set<Tutor> tutores = new HashSet<>();
}