package com.example.tutorias.dto.materia;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter 
public class CreateMateriaRequest {
    

    @NotBlank
    private String nombre;
}
