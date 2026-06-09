package com.example.tutorias.repository;

import  com.example.tutorias.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    boolean existsByLegajo(String legajo);

    boolean existsByDni(String dni);

}

