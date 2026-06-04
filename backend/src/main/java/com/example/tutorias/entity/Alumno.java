package com.example.tutorias.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter

@Entity
@Table(name = "Alumno")
@DiscriminatorValue("Alumno")
public class Alumno extends Persona {


    private String dni;
    private Boolean estado;
    
    
    @OneToMany(mappedBy = "alumno")
    private List<Inscripcion> inscripciones;

    @OneToMany(mappedBy = "alumno")
    private List<Feedback> feedbacksBrindados;

    
    @ManyToMany(mappedBy = "alumnos")
    private List<Tutoria> tutorias;


}