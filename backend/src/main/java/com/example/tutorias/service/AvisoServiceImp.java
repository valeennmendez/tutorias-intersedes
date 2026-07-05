package com.example.tutorias.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.tutorias.dto.avisos.AvisoResponseDTO;
import com.example.tutorias.dto.avisos.CrearAvisoRequestDTO;
import com.example.tutorias.entity.Aviso;
import com.example.tutorias.entity.Tutor;
import com.example.tutorias.entity.Tutoria;
import com.example.tutorias.repository.AlumnoRepository;
import com.example.tutorias.repository.AvisoRepository;
import com.example.tutorias.repository.TutorRepository;
import com.example.tutorias.repository.TutoriaRepository;

import jakarta.transaction.Transactional;

@Service
public class AvisoServiceImp implements AvisoService {

    private final AvisoRepository avisoRepository;

    private final TutoriaRepository tutoriaRepository;

    private final TutorRepository tutorRepository;

    private final AlumnoRepository alumnoRepository;

    public AvisoServiceImp(AvisoRepository avisoRepository, TutoriaRepository tutoriaRepository, TutorRepository tutorRepository, AlumnoRepository alumnoRepository) {
        this.avisoRepository = avisoRepository;
        this.tutoriaRepository = tutoriaRepository;
        this.tutorRepository = tutorRepository;
        this.alumnoRepository = alumnoRepository;
    }

    @Override
    @Transactional
    public AvisoResponseDTO crearAviso(CrearAvisoRequestDTO request, String emailTutorLogueado) {
        Tutoria tutoria = tutoriaRepository.findById(request.getTutoriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutoria no encontrada"));
    
        // 2. Buscamos al tutor real usando el email que vino del Token de seguridad
        Tutor tutor = tutorRepository.findByEmail(emailTutorLogueado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor no encontrado"));
        
        if(!tutoria.getTutor().getId().equals(tutor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para crear avisos en esta tutoria");
        }
        // FIX: NO LA SACO PERO LA COMENTO
        //validamos la regla de 4hs de anticipo para crear un aviso
        //LocalDateTime inicioTutoria = LocalDateTime.of(tutoria.getFecha(), tutoria.getHoraInicio());
        //if (LocalDateTime.now().isAfter(inicioTutoria.minusHours(4))) {
        //    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
        //        "Es demasiado tarde para enviar el aviso. Mínimo 4 horas de anticipación.");
        //}

        Aviso aviso = new Aviso();
        aviso.setTitulo(request.getTitulo());
        aviso.setContenido(request.getContenido());
        aviso.setTutoria(tutoria);
        aviso.setTutor(tutor);

        return toResponse(avisoRepository.save(aviso));
    
    }


    @Override
    @Transactional
    public List<AvisoResponseDTO> obtenerAvisosPorTutoria(Long tutoriaId) {
        // Solo traemos los avisos que no fueron eliminados (activo = true)
        if (!tutoriaRepository.existsById(tutoriaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutoría no encontrada");
        }
        
        List<Aviso> avisos = avisoRepository.findByTutoriaIdAndActivoTrue(tutoriaId);
        return avisos.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public List<AvisoResponseDTO> obtenerAvisosPorTutorLogueado(String emailTutor) {
        if (!tutorRepository.existsByEmail(emailTutor)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor no encontrado");
        }
        List<Aviso> avisos = avisoRepository.findByTutor_EmailAndActivoTrue(emailTutor);
        return avisos.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public List<AvisoResponseDTO> obtenerAvisosPorAlumnoLogueado(String emailAlumno) {
        if (!alumnoRepository.existsByEmail(emailAlumno)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado");
        }
        List<Aviso> avisos = avisoRepository.findByTutoria_Inscripciones_Alumno_EmailAndActivoTrue(emailAlumno);
        return avisos.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void eliminarAviso(Long avisoId) {
        Aviso aviso = avisoRepository.findById(avisoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aviso no encontrado"));
        
        // BAJA LÓGICA: En vez de hacer un repository.delete(), lo marcamos como inactivo
        aviso.setActivo(false);
        avisoRepository.save(aviso);
    }

    private AvisoResponseDTO toResponse(Aviso aviso) {
        AvisoResponseDTO response = new AvisoResponseDTO();
        response.setId(aviso.getId());
        response.setTitulo(aviso.getTitulo());
        response.setContenido(aviso.getContenido());
        response.setFechaCreacion(aviso.getCreatedAt());
        
        response.setTutoriaId(aviso.getTutoria().getId());
        response.setNombreTutoria(aviso.getTutoria().getNombre()); 
        
        response.setNombreTutor(aviso.getTutor().getNombre() + " " + aviso.getTutor().getApellido());
        
        return response;
    }
}
