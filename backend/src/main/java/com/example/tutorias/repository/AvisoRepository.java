package com.example.tutorias.repository;
import com.example.tutorias.entity.Aviso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AvisoRepository extends JpaRepository<Aviso, Long> {
    
    boolean existsByTituloAndTutoriaId(String titulo, Long tutoriaId);

    List<Aviso> findByTutoriaIdAndActivoTrue(Long tutoriaId);

    List<Aviso> findByTutor_EmailAndActivoTrue(String tutorEmail);

    
    List<Aviso> findByTutoria_Inscripciones_Alumno_EmailAndActivoTrue(String emailAlumno);

}

