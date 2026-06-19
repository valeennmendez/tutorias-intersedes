package com.example.tutorias.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.tutorias.entity.PostulacionTutor;

public interface PostulacionTutorService {

    PostulacionTutor registrarPostulacion(Long alumnoId, Long materiaId, Double notaAprobacion, String justificacion, MultipartFile archivoPdf);
    public List<PostulacionTutor> obtenerPostulacionesPorAlumno(Long alumnoId);
    public void actualizarEstadoPostulacion(Long postulacionId, String nuevoEstado);
    public void eliminarPostulacion(Long postulacionId);
}
