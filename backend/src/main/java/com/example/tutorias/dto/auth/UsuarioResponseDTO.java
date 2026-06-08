package com.example.tutorias.dto.auth;

import java.time.LocalDate;

import org.jspecify.annotations.Nullable;

import com.example.tutorias.entity.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String role;
    
    public UsuarioResponseDTO(Long id, String nombre, String apellido, String email, Role role) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.role = role.name(); // Convertir el enum a su representación de cadena
    }
}
