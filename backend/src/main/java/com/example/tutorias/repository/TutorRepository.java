package com.example.tutorias.repository;

import com.example.tutorias.entity.Tutor;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Optional<Tutor> findByEmail(String email); // Método para encontrar un tutor por su correo electrónico, si bien el email está en la entidad Persona jpa puede entender la herencia y buscar en la tabla correspondiente.

    boolean existsByEmail(String email); // Método para verificar si un tutor con un correo electrónico específico ya existe en la base de datos.
    Page<Tutor> findByEstadoTutorTrue(Pageable pageable); // Método para obtener una lista paginada de tutores filtrados por su estado (aprobado o no aprobado).

    // creamos un registro vacío en la tabla tutor para un alumno que se está promoviendo a tutor
    @Modifying
    @Query(value = "INSERT INTO tutor (id) VALUES (:id)", nativeQuery = true)
    void crearRegistroTutorVacio(@Param("id") Long id);

    // cambiamos el dtype de la tabla persona a 'Tutor' y el role a 'TUTOR' para un alumno que se está promoviendo a tutor
    @Modifying
    @Query(value = "UPDATE persona SET dtype = 'Tutor', role = 'TUTOR' WHERE id = :id", nativeQuery = true)
    void promoverDtypeATutor(@Param("id") Long id);
}
