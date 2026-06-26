package com.example.tutorias.dto.postulaciones;

import com.example.tutorias.entity.PostulacionTutor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostulacionTutorResponseDTO {
    private String id; 
    private String materia_id; 
    private String justificacion;
    private Double nota_aprobacion;
    private String status; 
    private String admin_comentario;
    private LocalDateTime created_at;
    private MateriaDto materia;
    private String sede_preferencia;
    private String modalidad_preferencia;

    // Sub-clase para mandar el nombre de la materia como pide el frontend
    @Getter
    @Setter
    public static class MateriaDto {
        private String nombre;
    }

    public static PostulacionTutorResponseDTO from(PostulacionTutor postulacion) {
        PostulacionTutorResponseDTO dto = new PostulacionTutorResponseDTO();
        dto.setId(postulacion.getId().toString());
        dto.setMateria_id(postulacion.getMateria().getId().toString());
        dto.setJustificacion(postulacion.getJustificacion());
        dto.setNota_aprobacion(postulacion.getNotaAprobacion());
        dto.setStatus(postulacion.getEstado().name().toLowerCase());
        dto.setAdmin_comentario(postulacion.getAdminComentario());
        dto.setCreated_at(postulacion.getCreatedAt());
        dto.setSede_preferencia(postulacion.getSedePreferencia());
        dto.setModalidad_preferencia(postulacion.getModalidadPreferencia().name());

        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre(postulacion.getMateria().getNombre());
        dto.setMateria(materiaDto);

        return dto;
    }
}