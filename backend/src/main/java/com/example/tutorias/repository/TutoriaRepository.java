package com.example.tutorias.repository;

import com.example.tutorias.entity.Tutoria;
import com.example.tutorias.entity.EstadoTutoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TutoriaRepository extends JpaRepository<Tutoria, Long> {

    List<Tutoria> findByAlumnosId(Long alumnoId);

    @Query("select count(a) from Tutoria t join t.alumnos a where t.id = :tutoriaId")
    long countAlumnosByTutoriaId(@Param("tutoriaId") Long tutoriaId);

    boolean existsByTutorIdAndFechaAndEstadoAndHoraInicioBeforeAndHoraFinAfter(
            Long tutorId,
            LocalDate fecha,
            EstadoTutoria estado,
            LocalTime horaFin,
            LocalTime horaInicio
    );
}
