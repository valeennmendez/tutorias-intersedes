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
@Table(name = "Tutor")
@DiscriminatorValue("Tutor")
public class Tutor extends Alumno { //IMPORTANTE: La clase Tutor hereda de Alumno, lo que significa que un tutor es un tipo específico de alumno. Esto permite que un tutor tenga todas las propiedades y comportamientos de un alumno, además de sus propias características específicas.

    @Column(nullable = true)
    private String titulo;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean estadoTutor;

    // 
    @Column(nullable = true, unique = true) 
    private String cuit;

    // La relación responde a que un tutor es aceptado por un administrador, y un administrador puede aceptar a varios tutores, por lo que se establece una relación ManyToOne
    @ManyToOne
    @JoinColumn(name = "administrador_id")
    private Administrador administrador;

    // Un tutor puede crear múltiples avisos, materias, certificados, tutorías y recibir múltiples feedbacks, por lo que se establecen relaciones OneToMany
    @OneToMany(mappedBy = "tutor")
    private List<Aviso> avisos;

    @ManyToMany
    @JoinTable(
        name = "tutor_materia",
        joinColumns = @JoinColumn(name = "tutor_id"),
        inverseJoinColumns = @JoinColumn(name = "materia_id")
    )
    private Set<Materia> materias = new HashSet<>();

    @OneToMany(mappedBy = "tutor")
    private List<Certificado> certificados;

    @OneToMany(mappedBy = "tutor")
    private List<Tutoria> tutoriasCreadas;
}