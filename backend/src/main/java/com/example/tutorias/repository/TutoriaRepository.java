package com.example.tutorias.repository;

import com.example.tutorias.entity.Tutoria;
import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.InscripcionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TutoriaRepository extends JpaRepository<Tutoria, Long>, JpaSpecificationExecutor<Tutoria> {

    // nuevo método para obtener tutorías por alumno y estado de inscripción
    List<Tutoria> findByInscripciones_Alumno_IdAndInscripciones_Status(Long alumnoId, InscripcionStatus status);

    boolean existsByTutorIdAndFechaAndEstadoAndHoraInicioBeforeAndHoraFinAfter(
            Long tutorId,
            LocalDate fecha,
            EstadoTutoria estado,
            LocalTime horaFin,
            LocalTime horaInicio
    );
}
