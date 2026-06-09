package com.example.tutorias.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter

@Entity
@Table(name = "Tutor")
@DiscriminatorValue("Tutor")
public class Tutor extends Persona {

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean estado;

    // 
    @Column(nullable = false, unique = true) 
    private String cuit;

    // La relación responde a que un tutor es aceptado por un administrador, y un administrador puede aceptar a varios tutores, por lo que se establece una relación ManyToOne
    @ManyToOne
    @JoinColumn(name = "administrador_id")
    private Administrador administrador;

    // Un tutor puede crear múltiples avisos, materias, certificados, tutorías y recibir múltiples feedbacks, por lo que se establecen relaciones OneToMany
    @OneToMany(mappedBy = "tutor")
    private List<Aviso> avisos;

    @OneToMany(mappedBy = "tutor")
    private List<Materia> materias;

    @OneToMany(mappedBy = "tutor")
    private List<Certificado> certificados;

    @OneToMany(mappedBy = "tutor")
    private List<Tutoria> tutoriasCreadas;

    @OneToMany(mappedBy = "tutor")
    private List<Feedback> feedbacksRecibidos;
}