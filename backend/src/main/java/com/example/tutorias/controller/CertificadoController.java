package com.example.tutorias.controller;

import com.example.tutorias.security.UserDetailsImpl;
import com.example.tutorias.service.CertificadoService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certificados")
public class CertificadoController {

    private final CertificadoService certificadoService;

    public CertificadoController(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
    }

    @GetMapping("/mis-certificados/pdf")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<byte[]> descargarMiCertificado(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        byte[] pdf = certificadoService.generarPdfCertificado(userDetails.getUsername());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("certificado-tutor.pdf")
                                .build()
                                .toString())
                .body(pdf);
    }
}
