package com.example.tutorias.dto.tutoria;

import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.Sede;
import com.example.tutorias.entity.Tutoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TutoriaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer cupo;
    private String ubicacion;
    private String linkVirtual;
    private String linkDrive;
    private ModalidadTutoria modalidad;
    private EstadoTutoria estado;
    private Long tutorId;
    private String tutorNombre;
    private Long materiaId;
    private String materiaNombre;
    private long cantidadInscriptos;
    private Sede sede;

    public static TutoriaResponse from(Tutoria tutoria, long cantidadInscriptos) {
        return TutoriaResponse.builder()
                .id(tutoria.getId())
                .nombre(tutoria.getNombre())
                .descripcion(tutoria.getDescripcion())
                .fecha(tutoria.getFecha())
                .horaInicio(tutoria.getHoraInicio())
                .horaFin(tutoria.getHoraFin())
                .cupo(tutoria.getCupo())
                .ubicacion(tutoria.getUbicacion())
                .linkVirtual(tutoria.getLinkVirtual())
                .linkDrive(tutoria.getLinkDrive())
                .modalidad(tutoria.getModalidad())
                .estado(tutoria.getEstado())
                .tutorId(tutoria.getTutor() != null ? tutoria.getTutor().getId() : null)
                .tutorNombre(tutoria.getTutor() != null ? tutoria.getTutor().getNombre() : null)
                .materiaId(tutoria.getMateria() != null ? tutoria.getMateria().getId() : null)
                .materiaNombre(tutoria.getMateria() != null ? tutoria.getMateria().getNombre() : null)
                .cantidadInscriptos(cantidadInscriptos)
                .sede(tutoria.getSede() != null ? tutoria.getSede() : null)
                .build();
    }
}
