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

    private String titulo;
    private Boolean estado;
    private String cuit;

    @ManyToOne
    @JoinColumn(name = "administrador_id")
    private Administrador administrador;

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