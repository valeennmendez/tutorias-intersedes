package com.example.tutorias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter

@Entity
@Table(name = "Alumno")
@DiscriminatorValue("Alumno")
public class Alumno extends Persona {

    public Alumno(String nombre, String apellido, String email, String password, LocalDate fechanacimiento, String direccion, Role role, String dni, Boolean estado, String legajo, int anioInicio, Carrera carrera) {
        super(nombre, apellido, email, password, fechanacimiento, direccion, role);
        this.dni = dni;
        this.estado = estado;
        this.legajo = legajo;
        this.anioInicio = anioInicio;
        this.carrera = carrera;
    }

    public Alumno() {
        super();
    }

    @Column(nullable = false, unique = true)
    private String dni;

    // Asumiendo que el estado se refiere a si el alumno está activo o no, se establece un valor predeterminado de true
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean estado;
    
    // Agrego columnas que aparecen en el trello.
    @Column(nullable = false)
    private String legajo;

    @Column(nullable = false)
    private int anioInicio;

    @ManyToOne(fetch = FetchType.LAZY) //carga la carrera sólo si se le pide, por simplicidad asumimos que un alumno sólo tiene una carrera
    @JoinColumn(name = "carrera_id", nullable = false) //crea la fk en la tabla alumno
    private Carrera carrera; //en el trello se indica que las carreras aparecen precargadas en la bd

    // Un alumno puede tener múltiples inscripciones, feedbacks brindadas
    @OneToMany(mappedBy = "alumno")
    private List<Inscripcion> inscripciones;

    @OneToMany(mappedBy = "alumno")
    private List<Feedback> feedbacksBrindados;

    // Un alumno puede estar inscrito en múltiples tutorías y una tutoría puede tener múltiples alumnos, por lo que se establece una relación ManyToMany
    @ManyToMany(mappedBy = "alumnos")
    private List<Tutoria> tutorias;
}