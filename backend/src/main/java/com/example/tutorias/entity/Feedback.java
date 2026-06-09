package com.example.tutorias.entity;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer calificacion;
    private Boolean es_anonimo;
    private String comentario;
    private Date fecha;
    
    @ManyToOne
    @JoinColumn(name = "alumno_id") // Brinda
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "tutor_id") // Recibe
    private Tutor tutor;
}