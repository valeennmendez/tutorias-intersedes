package com.example.tutorias.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import  com.example.tutorias.entity.Alumno;


public interface AlumnoRepository extends JpaRepository<Alumno, Long> {



}

