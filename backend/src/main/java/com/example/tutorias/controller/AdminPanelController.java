package com.example.tutorias.controller;
import com.example.tutorias.dto.admin.PostulacionAdminDTO;
import com.example.tutorias.dto.admin.TutorAdminDTO;
import com.example.tutorias.service.AdminPanelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/panel")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPanelController {

    private final AdminPanelService adminPanelService;

    public AdminPanelController(AdminPanelService adminPanelService) {
        this.adminPanelService = adminPanelService;
    }

    @GetMapping("/tutores-aprobados")
    public ResponseEntity<Page<TutorAdminDTO>> listarTutoresAprobados(
            @PageableDefault(size = 10, sort = "apellido", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(adminPanelService.obtenerTutoresAprobados(pageable));
    }

    @GetMapping("/postulaciones-pendientes")
    public ResponseEntity<Page<PostulacionAdminDTO>> listarPostulacionesPendientes(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminPanelService.obtenerPostulacionesPendientes(pageable));
    }

    @GetMapping("/tutorias-activas")
    public ResponseEntity<Page<com.example.tutorias.dto.tutoria.TutoriaResponse>> listarTutoriasActivas(
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(adminPanelService.obtenerTutoriasActivas(pageable));
    }
}