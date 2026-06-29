package com.example.tutorias.service;

import com.example.tutorias.dto.materia.MateriaResponse;
import com.example.tutorias.entity.Materia;
import com.example.tutorias.repository.MateriaRepository;
import com.example.tutorias.repository.TutorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final TutorRepository tutorRepository;

    public MateriaService(MateriaRepository materiaRepository, TutorRepository tutorRepository) {
        this.materiaRepository = materiaRepository;
        this.tutorRepository = tutorRepository;
    }

    @Transactional(readOnly = true)
    public List<MateriaResponse> obtenerMateriasPorTutor(Long tutorId) {
        var tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor no encontrado"));

        if (!Boolean.TRUE.equals(tutor.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tutor no está aprobado");
        }

        return tutor.getMaterias().stream()
                .map(MateriaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MateriaResponse> obtenerTodasMaterias() {
        List<Materia> materias = materiaRepository.findAll();
        return materias.stream()
                .map(MateriaResponse::from)
                .toList();
    }

    public MateriaResponse crearMateria(String nombre) {
        if (materiaRepository.existsByNombre(nombre)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La materia ya existe");
        }

        Materia materia = new Materia();
        materia.setNombre(nombre);
        materiaRepository.save(materia);

        return MateriaResponse.from(materia);
    }

    public MateriaResponse actualizarMateria(Long id, String nombre) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada"));

        if (materiaRepository.existsByNombre(nombre)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La materia ya existe");
        }

        materia.setNombre(nombre);
        materiaRepository.save(materia);

        return MateriaResponse.from(materia);
    }    

    public void eliminarMateria(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada"));

        materiaRepository.delete(materia);
    }

    
}
