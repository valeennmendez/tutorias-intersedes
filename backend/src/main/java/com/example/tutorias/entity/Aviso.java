package com.example.tutorias.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
public class Aviso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
	private String cuerpo;
    

    //por si el aviso es a todo el instituto
	private boolean institucional;

	private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @ManyToOne
	@JoinColumn(name = "usuario_id")
	private Persona creador;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @ManyToOne
	@JoinColumn(name = "tutoria_id") 
	@JsonIgnore
	private Tutoria tutoria;
}
    