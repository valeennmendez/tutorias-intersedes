package com.example.tutorias.entity;

import jakarta.persistence.Id;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Carrera")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Esto hace que SQL asigne el ID automáticamente
    private Long id;

    @Column(nullable = false)
    private String nombre;

    // Constructor vacío requerido por JPA
    public Carrera() {}

    public Carrera(String nombre) {
        this.nombre = nombre;
    }

    public Carrera orElseThrow(Object object) {
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }
}
