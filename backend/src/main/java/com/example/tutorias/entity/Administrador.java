package com.example.tutorias.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Administrador extends Persona {
    
    @OneToMany(mappedBy = "administrador")
    private List<Tutor> tutoresAceptados;
}