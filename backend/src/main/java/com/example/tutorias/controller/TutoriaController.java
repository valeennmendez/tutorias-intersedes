package com.example.tutorias.controller;

import com.example.tutorias.dto.tutoria.CantidadInscriptosResponse;
import com.example.tutorias.dto.tutoria.CrearTutoriaRequest;
import com.example.tutorias.dto.tutoria.TutoriaResponse;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.Sede;
import com.example.tutorias.service.TutoriaService;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tutorias")
public class TutoriaController {

    private final TutoriaService tutoriaService;

    public TutoriaController(TutoriaService tutoriaService) {
        this.tutoriaService = tutoriaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TutoriaResponse crearTutoria(@Valid @RequestBody CrearTutoriaRequest request) {
        return tutoriaService.crearTutoria(request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<Void> eliminarTutoria(@PathVariable Long id, Principal principal) {
        tutoriaService.eliminarTutoria(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<TutoriaResponse> actualizarTutoria(
            @PathVariable Long id,
            Principal principal,
            @Valid @RequestBody CrearTutoriaRequest request) {
        return ResponseEntity.ok(tutoriaService.actualizarTutoria(id, principal.getName(), request));
    }

    /* * COMENTADO POR Dounchers - RF-03
     * Este método fue reemplazado por el nuevo obtenerTutorias() de abajo, 
     * que ya incluye la lógica de traer todas las tutorías si no se le pasan filtros,
     * evitando el error de "Ambiguous mapping" de Spring Boot.
     *
    @GetMapping
    public List<TutoriaResponse> obtenerTutorias() {
        return tutoriaService.obtenerTutorias();
    }
    */  

    @GetMapping("/alumno/{alumnoId}")
    public List<TutoriaResponse> obtenerTutoriasPorAlumno(@PathVariable Long alumnoId) {
        return tutoriaService.obtenerTutoriasPorAlumno(alumnoId);
    }

    @GetMapping("/{tutoriaId}/cantidad-inscriptos")
    public CantidadInscriptosResponse obtenerCantidadInscriptos(@PathVariable Long tutoriaId) {
        long cantidad = tutoriaService.obtenerCantidadInscriptos(tutoriaId);
        return new CantidadInscriptosResponse(tutoriaId, cantidad);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ALUMNO') or hasRole('TUTOR') or hasRole('ADMIN')") // Solo usuarios autenticados pueden acceder, sin importar el rol específico
    public ResponseEntity<TutoriaResponse> obtenerTutoriaPorId(@PathVariable Long id) {
        TutoriaResponse response = tutoriaService.obtenerTutoriaPorId(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ALUMNO') or hasRole('TUTOR') or hasRole('ADMIN')") // Solo usuarios autenticados pueden acceder, sin importar el rol específico
    public ResponseEntity<List<TutoriaResponse>> obtenerTutorias(
            @RequestParam(required = false) String materia,
            @RequestParam(required = false) Sede sede,
            @RequestParam(required = false) ModalidadTutoria modalidad
    ) {
        List<TutoriaResponse> resultados = tutoriaService.buscarTutoriasConFiltros(materia, sede, modalidad);
        return ResponseEntity.ok(resultados);
    }

    @PostMapping("/{id}/agregar-link")
    @PreAuthorize("hasRole('TUTOR') or hasRole('ADMIN')")
    public ResponseEntity<TutoriaResponse> agregarLinkDrive(@PathVariable Long id, @RequestBody String linkDrive) {
        TutoriaResponse response = tutoriaService.agregarLink(id, linkDrive);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/actualizar-link")
    @PreAuthorize("hasRole('TUTOR') or hasRole('ADMIN')")
    public ResponseEntity<TutoriaResponse> actualizarLink(@PathVariable Long id, @RequestBody String link) {
        TutoriaResponse response = tutoriaService.actualizarLink(id, link);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/eliminar-link")
    @PreAuthorize("hasRole('TUTOR') or hasRole('ADMIN')")
    public ResponseEntity<TutoriaResponse> eliminarLink(@PathVariable Long id) {
        TutoriaResponse response = tutoriaService.eliminarLink(id);
        return ResponseEntity.ok(response);
    }
    
}
