package com.example.tutorias.entity;

import jakarta.persistence.*;import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "postulacion_tutor")
public class PostulacionTutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el alumno que se postula (en la BD es user_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Alumno postulante;

    // Relación con la materia a la que aplica
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @Column(columnDefinition = "TEXT")
    private String justificacion;

    @Column(name = "nota_aprobacion")
    private Double notaAprobacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private PostulacionTutorEstado estado; 

    @Column(name = "admin_comentario", columnDefinition = "TEXT")
    private String adminComentario;

    // El administrador que revisó la postulación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Persona revisor;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "pdf_path")
    private String pdfPath;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = PostulacionTutorEstado.PENDIENTE;
        }
    }
}