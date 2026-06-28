package com.example.tutorias.service;
import com.example.tutorias.dto.auth.CrearAdminRequestDTO;
import com.example.tutorias.dto.auth.LoginRequestDTO;
import com.example.tutorias.dto.auth.LoginResponseDTO;
import com.example.tutorias.dto.auth.RegistroRequestDTO;
import com.example.tutorias.dto.auth.UsuarioResponseDTO;

public interface AuthService {
    void registerUser(RegistroRequestDTO registroRequest); 
    void crearAdmin(CrearAdminRequestDTO crearAdminRequest);
    LoginResponseDTO loginUser(LoginRequestDTO loginRequest);
    UsuarioResponseDTO getUsuarioById(Long id);
}
