package com.example.tutorias.service;
import com.example.tutorias.dto.avisos.AvisoResponseDTO;
import com.example.tutorias.dto.avisos.CrearAvisoRequestDTO;
import java.util.List;


public interface AvisoService {
    public AvisoResponseDTO crearAviso(CrearAvisoRequestDTO request, String emailTutorLogueado);
    public List<AvisoResponseDTO> obtenerAvisosPorTutoria(Long tutoriaId);
    public List<AvisoResponseDTO> obtenerAvisosPorTutorLogueado(String emailTutor);
    public List<AvisoResponseDTO> obtenerAvisosPorAlumnoLogueado(String alumnoEmail);
    public void eliminarAviso(Long avisoId);
}
