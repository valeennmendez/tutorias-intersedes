package com.example.tutorias.controller;

import com.example.tutorias.dto.postulaciones.PostulacionTutorResponseDTO;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.PostulacionTutor;
import com.example.tutorias.security.UserDetailsImpl;
import com.example.tutorias.service.PostulacionTutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/postulaciones")
public class PostulacionTutorController {

    @Autowired
    private PostulacionTutorService postulacionTutorService;

    // 1. Crear nueva postulación (Solo alumnos)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUMNO')") // Protegemos el endpoint
    public ResponseEntity<PostulacionTutorResponseDTO> crearPostulacion(
            @RequestParam("materia_id") Long materiaId,
            @RequestParam("nota_aprobacion") Double notaAprobacion,
            @RequestParam String justificacion,
            @RequestParam("sede_preferencia") String sedePreferencia,
            @RequestParam ModalidadTutoria modalidad,
            @RequestParam("archivo_pdf") MultipartFile archivoPdf,
            @AuthenticationPrincipal UserDetailsImpl userDetails // obtener el usuario logueado para asociar la postulación al alumno correcto
    ) {
        // Sacamos el ID directamente del token de sesión, no del frontend
        Long alumnoId = userDetails.getId(); 

        PostulacionTutorResponseDTO respuesta = postulacionTutorService.registrarPostulacion(
                alumnoId, materiaId, notaAprobacion, justificacion, sedePreferencia, modalidad, archivoPdf
        );

        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // 2. Ver mis postulaciones (El alumno ve su historial)
    @GetMapping("/mis-postulaciones")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<List<PostulacionTutorResponseDTO>> obtenerMisPostulaciones(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long alumnoId = userDetails.getId();
        List<PostulacionTutorResponseDTO> historialDTO = postulacionTutorService.obtenerPostulacionesPorAlumno(alumnoId);
        // el servicio ya devuelve una lista de DTOs, así que no necesitamos mapearlo , solo retornar
        return ResponseEntity.ok(historialDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<String> eliminarPostulacion(
            @PathVariable("id") Long postulacionId,
            @AuthenticationPrincipal UserDetailsImpl userDetails // Extraemos quién está logueado
    ) {
        Long alumnoId = userDetails.getId(); // Sacamos el ID seguro del token
        
        // Le mandamos al servicio qué queremos borrar, y QUIÉN lo quiere borrar
        postulacionTutorService.eliminarPostulacion(postulacionId, alumnoId);
        
        return ResponseEntity.ok("Postulación eliminada con éxito");
    }
}