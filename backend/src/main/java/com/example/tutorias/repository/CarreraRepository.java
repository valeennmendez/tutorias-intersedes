package com.example.tutorias.repository;

import org.springframework.stereotype.Repository;
import com.example.tutorias.entity.Carrera;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    // Método para buscar una carrera por su nombre
    Optional<Carrera> findByNombre(String nombre);
    
}
