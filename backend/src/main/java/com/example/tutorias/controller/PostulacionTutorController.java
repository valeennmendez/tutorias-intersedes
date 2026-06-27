package com.example.tutorias.controller;

import com.example.tutorias.dto.postulaciones.PostulacionTutorResponseDTO;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.PostulacionTutorEstado;
import com.example.tutorias.security.UserDetailsImpl;
import com.example.tutorias.service.PostulacionTutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        return ResponseEntity.ok(historialDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PostulacionTutorResponseDTO>> obtenerTodasPostulaciones() {
        return ResponseEntity.ok(postulacionTutorService.obtenerTodasPostulaciones());
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> actualizarEstadoPostulacion(
            @PathVariable("id") Long postulacionId,
            @RequestParam("estado") PostulacionTutorEstado estado,
            @RequestParam(value = "comentario", required = false) String comentario,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long adminId = userDetails.getId();
        postulacionTutorService.actualizarEstadoPostulacion(postulacionId, estado, adminId, comentario);
        return ResponseEntity.ok("Estado de postulación actualizado");
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ALUMNO')")
    public ResponseEntity<Resource> descargarPdfPostulacion(
            @PathVariable("id") Long postulacionId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws MalformedURLException {
        String rutaPdf = postulacionTutorService.obtenerRutaPdfPostulacion(postulacionId);
        Path path = Paths.get(rutaPdf);
        UrlResource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=analitico-" + postulacionId + ".pdf")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<String> eliminarPostulacion(
            @PathVariable("id") Long postulacionId,
            @AuthenticationPrincipal UserDetailsImpl userDetails // Extraemos quién está logueado
    ) {
        Long alumnoId = userDetails.getId(); // Sacamos el ID seguro del token
        postulacionTutorService.eliminarPostulacion(postulacionId, alumnoId);
        return ResponseEntity.ok("Postulación eliminada con éxito");
    }
}