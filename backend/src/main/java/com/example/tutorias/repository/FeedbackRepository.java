package com.example.tutorias.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.tutorias.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Para que el Tutor vea las reseñas de su clase
    Page<Feedback> findByInscripcionTutoriaId(Long tutoriaId, Pageable pageable);

    // Para que el Alumno vea todo el feedback que dejó históricamente
    Page<Feedback> findByInscripcionAlumnoEmail(String emailAlumno, Pageable pageable);

    // Traer todos los feedbacks dirigidos a un tutor específico
    Page<Feedback> findByInscripcion_Tutoria_Tutor_Id(Long tutorId, Pageable pageable);

    // Calcular el promedio de estrellas de un tutor
    @Query("SELECT AVG(f.calificacion) FROM Feedback f WHERE f.inscripcion.tutoria.tutor.id = :tutorId")
    Double obtenerPromedioEstrellasPorTutor(Long tutorId);
}
