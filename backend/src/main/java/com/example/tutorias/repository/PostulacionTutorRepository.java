package com.example.tutorias.repository;

import com.example.tutorias.entity.PostulacionTutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PostulacionTutorRepository extends JpaRepository<PostulacionTutor, Long> {

    // para obtener las postulaciones de un alumno específico
    List<PostulacionTutor> findByPostulanteId(Long alumnoId);

    List<PostulacionTutor> findByMateriaId(Long materiaId);
    
    List<PostulacionTutor> findByEstado(String estado);
}
