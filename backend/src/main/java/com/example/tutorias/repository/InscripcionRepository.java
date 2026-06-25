package com.example.tutorias.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tutorias.entity.Inscripcion;
import com.example.tutorias.entity.InscripcionStatus;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    
    // Busca si el alumno ya intentó inscribirse a la misma tutoria
    Optional<Inscripcion> findByTutoriaIdAndAlumnoEmail(Long tutoriaId, String alumnoEmail);

    // Cuenta la cantidad de inscripciones activas para una tutoria específica
    long countByTutoriaIdAndStatus(Long tutoriaId, InscripcionStatus status);

    //trae todas las inscripciones de un alumno específico, útil para front
    List<Inscripcion> findByAlumnoEmailAndStatus(String alumnoEmail, InscripcionStatus status);

    // Método para ver inscripciones de una tutoría específica, útil para el front
    List<Inscripcion> findByTutoriaIdAndStatus(Long tutoriaId, InscripcionStatus status);

    //para que un alumno pueda ver todas sus inscripciones, útil para el front
    List<Inscripcion> findByAlumnoEmail(String alumnoEmail);
    

}
