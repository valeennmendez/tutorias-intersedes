package com.example.tutorias.dto.auth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Data;


@Data
public class LoginRequestDTO {
    
    @NotBlank(message = "El correo es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@comunidad\\.unnoba\\.edu\\.ar$",
             message = "El correo debe ser @comunidad.unnoba.edu.ar")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

}
