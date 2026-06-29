package com.example.tutorias.config;

import com.example.tutorias.entity.Administrador;
import com.example.tutorias.entity.Role;
import com.example.tutorias.repository.AdministradorRepository;
import com.example.tutorias.repository.PersonaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataSeedConfig {

    @Bean
    CommandLineRunner initAdmin(PersonaRepository personaRepository, 
                                AdministradorRepository administradorRepository, 
                                PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@comunidad.unnoba.edu.ar"; //tiene que tener el mismo @ porque sino no s patea el login
            
            // Si el correo no existe en la base, creamos el admin
            if (!personaRepository.existsByEmail(adminEmail)) {
                Administrador admin = new Administrador();
                admin.setNombre("Super");
                admin.setApellido("Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin")); // Contraseña por defecto
                admin.setRole(Role.ADMIN);
                
                // Llená los campos obligatorios que hereda de Persona

                admin.setDireccion("Sede Central");
                admin.setFechanacimiento(LocalDate.of(1990, 1, 1));

                administradorRepository.save(admin);
            }
        };
    }
}