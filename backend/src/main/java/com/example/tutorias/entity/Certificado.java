package com.example.tutorias.entity;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

@Entity
public class Certificado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date fechaEmision;
    private Integer horas;
    private Integer total_tutorias;
    private Double calificacion_promedio;
    private String nombre_tutor;
    private String codigo_verificacion;
    
    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;
}