package com.example.tutorias.repository;

import com.example.tutorias.entity.PostulacionTutor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.tutorias.entity.PostulacionTutorEstado;

import java.util.List;

public interface PostulacionTutorRepository extends JpaRepository<PostulacionTutor, Long> {

    // para obtener las postulaciones de un alumno específico
    List<PostulacionTutor> findByPostulanteId(Long alumnoId);

    List<PostulacionTutor> findByMateriaId(Long materiaId);
    
    List<PostulacionTutor> findByEstado(PostulacionTutorEstado estado);
    Page<PostulacionTutor> findByEstado(PostulacionTutorEstado estado, Pageable pageable);
}
