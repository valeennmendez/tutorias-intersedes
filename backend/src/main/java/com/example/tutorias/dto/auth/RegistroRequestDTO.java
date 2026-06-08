package com.example.tutorias.dto.auth;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Data
public class RegistroRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    // Validación para asegurar que el email tenga el formato correcto y termine con @comunidad.unnoba.edu.ar
    @NotBlank(message = "El correo es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@comunidad\\.unnoba\\.edu\\.ar$",
             message = "El correo debe ser @comunidad.unnoba.edu.ar")
    private String email;

    @NotBlank(message = "El legajo es obligatorio")
    private String legajo;

    @NotBlank(message = "La carrera es obligatoria")
    private String carrera;

    @NotNull(message = "El año de inicio es obligatorio")
    private int anioInicio;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd") // Asegura que el formato de fecha sea correcto
    private LocalDate fechanacimiento; 
    
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    
}
