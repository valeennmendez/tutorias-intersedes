package com.example.tutorias.repository;

import com.example.tutorias.entity.Tutor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Optional<Tutor> findByEmail(String email); // Método para encontrar un tutor por su correo electrónico, si bien el email está en la entidad Persona jpa puede entender la herencia y buscar en la tabla correspondiente.

    boolean existsByEmail(String email); // Método para verificar si un tutor con un correo electrónico específico ya existe en la base de datos.
}
