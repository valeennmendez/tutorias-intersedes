package com.example.tutorias.dto.admin;

import com.example.tutorias.entity.PostulacionTutor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostulacionAdminDTO {
    private Long id;
    
    // Identidad del Alumno postulado
    private String postulanteNombre;
    private String postulanteEmail;
    
    // Datos de la solicitud
    private String materia;
    private LocalDateTime fechaSolicitud;
    private String justificacion;
    private Double notaAprobacion;
    private String pdfPath;
    private String sedePreferencia;
    private String modalidadPreferencia;

    public static PostulacionAdminDTO from(PostulacionTutor p) {
        PostulacionAdminDTO dto = new PostulacionAdminDTO();
        dto.setId(p.getId());
        
        dto.setPostulanteNombre(p.getPostulante().getNombre() + " " + p.getPostulante().getApellido());
        dto.setPostulanteEmail(p.getPostulante().getEmail());
        
        dto.setMateria(p.getMateria().getNombre());
        dto.setFechaSolicitud(p.getCreatedAt());
        dto.setJustificacion(p.getJustificacion());
        dto.setNotaAprobacion(p.getNotaAprobacion());
        dto.setPdfPath(p.getPdfPath());
        dto.setSedePreferencia(p.getSedePreferencia());
        dto.setModalidadPreferencia(p.getModalidadPreferencia().name());
        
        return dto;
    }
}