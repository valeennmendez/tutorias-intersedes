package com.example.tutorias.service;

import com.example.tutorias.entity.Certificado;
import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.Tutor;
import com.example.tutorias.entity.Tutoria;
import com.example.tutorias.repository.CertificadoRepository;
import com.example.tutorias.repository.FeedbackRepository;
import com.example.tutorias.repository.TutorRepository;
import com.example.tutorias.repository.TutoriaRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class CertificadoService {

    private final TutorRepository tutorRepository;
    private final TutoriaRepository tutoriaRepository;
    private final FeedbackRepository feedbackRepository;
    private final CertificadoRepository certificadoRepository;

    public CertificadoService(
            TutorRepository tutorRepository,
            TutoriaRepository tutoriaRepository,
            FeedbackRepository feedbackRepository,
            CertificadoRepository certificadoRepository) {
        this.tutorRepository = tutorRepository;
        this.tutoriaRepository = tutoriaRepository;
        this.feedbackRepository = feedbackRepository;
        this.certificadoRepository = certificadoRepository;
    }

    @Transactional
    public byte[] generarPdfCertificado(String emailTutor) {
        Tutor tutor = tutorRepository.findByEmail(emailTutor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor no encontrado"));

        if (!Boolean.TRUE.equals(tutor.getEstadoTutor())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tutor no esta aprobado");
        }

        List<Tutoria> tutoriasFinalizadas = tutoriaRepository
                .findByTutorIdAndEstadoOrderByFechaAscHoraInicioAsc(tutor.getId(), EstadoTutoria.FINALIZADA);

        if (tutoriasFinalizadas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tutor no tiene tutorias finalizadas para certificar");
        }

        int horasCumplidas = calcularHorasCumplidas(tutoriasFinalizadas);
        if (horasCumplidas <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tutor no tiene horas cumplidas para certificar");
        }

        double promedio = promedioCalificacion(tutor.getId());
        Certificado certificado = crearCertificado(tutor, tutoriasFinalizadas.size(), horasCumplidas, promedio);
        Certificado guardado = certificadoRepository.save(certificado);

        return construirPdf(guardado);
    }

    private Certificado crearCertificado(Tutor tutor, int totalTutorias, int horasCumplidas, double promedio) {
        Certificado certificado = new Certificado();
        certificado.setFechaEmision(Date.valueOf(LocalDate.now()));
        certificado.setHoras(horasCumplidas);
        certificado.setTotal_tutorias(totalTutorias);
        certificado.setCalificacion_promedio(promedio);
        certificado.setNombre_tutor(nombreCompleto(tutor));
        certificado.setCodigo_verificacion(UUID.randomUUID().toString());
        certificado.setTutor(tutor);
        return certificado;
    }

    private int calcularHorasCumplidas(List<Tutoria> tutorias) {
        long minutos = tutorias.stream()
                .filter(tutoria -> tutoria.getHoraInicio() != null && tutoria.getHoraFin() != null)
                .filter(tutoria -> tutoria.getHoraInicio().isBefore(tutoria.getHoraFin()))
                .mapToLong(tutoria -> Duration.between(tutoria.getHoraInicio(), tutoria.getHoraFin()).toMinutes())
                .sum();

        return (int) Math.ceil(minutos / 60.0);
    }

    private double promedioCalificacion(Long tutorId) {
        Double promedio = feedbackRepository.obtenerPromedioEstrellasPorTutor(tutorId);
        return promedio != null ? promedio : 0.0;
    }

    private byte[] construirPdf(Certificado certificado) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDType1Font tituloFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font textoFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                escribirLinea(content, tituloFont, 22, 160, 720, "Certificado de Mentoria");
                escribirLinea(content, textoFont, 13, 80, 665, "Red de Tutorias Inter-Sedes");
                escribirLinea(content, textoFont, 12, 80, 620, "Se certifica que:");
                escribirLinea(content, tituloFont, 18, 80, 585, certificado.getNombre_tutor());
                escribirLinea(content, textoFont, 12, 80, 545, "ha cumplido horas de mentoria en la plataforma.");
                escribirLinea(content, textoFont, 12, 80, 500, "Horas certificadas: " + certificado.getHoras());
                escribirLinea(content, textoFont, 12, 80, 475, "Tutorias finalizadas: " + certificado.getTotal_tutorias());
                escribirLinea(content, textoFont, 12, 80, 450, "Calificacion promedio: " + String.format("%.2f", certificado.getCalificacion_promedio()));
                escribirLinea(content, textoFont, 12, 80, 425, "Fecha de emision: " + certificado.getFechaEmision());
                escribirLinea(content, textoFont, 10, 80, 365, "Codigo de verificacion: " + certificado.getCodigo_verificacion());
                escribirLinea(content, textoFont, 10, 80, 335, "Certificado ID: " + certificado.getId());
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el certificado PDF");
        }
    }

    private void escribirLinea(PDPageContentStream content, PDType1Font font, int fontSize, float x, float y, String texto)
            throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(normalizarTexto(texto));
        content.endText();
    }

    private String nombreCompleto(Tutor tutor) {
        String nombre = tutor.getNombre() != null ? tutor.getNombre() : "";
        String apellido = tutor.getApellido() != null ? tutor.getApellido() : "";
        return (nombre + " " + apellido).trim();
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.replaceAll("[^\\x20-\\x7E]", "");
    }
}
