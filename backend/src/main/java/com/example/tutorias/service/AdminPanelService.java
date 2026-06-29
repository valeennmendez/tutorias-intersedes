package com.example.tutorias.service;

import com.example.tutorias.dto.tutoria.TutoriaResponse;
import com.example.tutorias.dto.admin.PostulacionAdminDTO;
import com.example.tutorias.dto.admin.TutorAdminDTO;
import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.PostulacionTutorEstado;
import com.example.tutorias.repository.PostulacionTutorRepository;
import com.example.tutorias.repository.TutorRepository;
import com.example.tutorias.repository.TutoriaRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminPanelService {

    private final TutorRepository tutorRepository;
    private final PostulacionTutorRepository postulacionRepository;
    private final TutoriaRepository tutoriaRepository;

    public AdminPanelService(TutorRepository tutorRepository, 
                             PostulacionTutorRepository postulacionRepository, 
                             TutoriaRepository tutoriaRepository) {
        this.tutorRepository = tutorRepository;
        this.postulacionRepository = postulacionRepository;
        this.tutoriaRepository = tutoriaRepository;
    }

    @Transactional(readOnly = true)
    public Page<TutorAdminDTO> obtenerTutoresAprobados(Pageable pageable) {
        return tutorRepository.findByEstadoTrue(pageable)
                .map(TutorAdminDTO::from);
    }
    
    @Transactional(readOnly = true)
    public Page<PostulacionAdminDTO> obtenerPostulacionesPendientes(Pageable pageable) {
        return postulacionRepository.findByEstado(PostulacionTutorEstado.PENDIENTE, pageable)
                .map(PostulacionAdminDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<TutoriaResponse> obtenerTutoriasActivas(Pageable pageable) {
        return tutoriaRepository.findByEstado(EstadoTutoria.ACTIVA, pageable)
                .map(tutoria -> TutoriaResponse.builder()
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
                        .tutorNombre(tutoria.getTutor() != null ? tutoria.getTutor().getNombre() + " " + tutoria.getTutor().getApellido() : "Sin asignar")
                        .materiaId(tutoria.getMateria().getId())
                        .materiaNombre(tutoria.getMateria().getNombre())
                        .cantidadInscriptos(tutoria.getInscripciones() != null ? tutoria.getInscripciones().size() : 0)
                        .sede(tutoria.getSede())
                        .build()
                );
    }
}