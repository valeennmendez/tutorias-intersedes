package com.example.tutorias.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter

@Entity
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    // Una materia puede tener asignadas muchas tutorías
    @OneToMany(mappedBy = "materia")
    private List<Tutoria> tutorias;
}