package com.example.tutorias.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.PostulacionTutor;
import com.example.tutorias.entity.PostulacionTutorEstado;
import com.example.tutorias.dto.postulaciones.PostulacionTutorResponseDTO;

public interface PostulacionTutorService {

    PostulacionTutorResponseDTO registrarPostulacion(Long alumnoId, Long materiaId, Double notaAprobacion, String justificacion, String sedePreferencia, ModalidadTutoria modalidad, MultipartFile archivoPdf);
    public List<PostulacionTutorResponseDTO> obtenerPostulacionesPorAlumno(Long alumnoId);
    public List<PostulacionTutorResponseDTO> obtenerTodasPostulaciones();
    public String obtenerRutaPdfPostulacion(Long postulacionId);
    public void actualizarEstadoPostulacion(Long postulacionId, PostulacionTutorEstado nuevoEstado, Long adminId, String adminComentario);
    public void eliminarPostulacion(Long postulacionId, Long alumnoId);
}
