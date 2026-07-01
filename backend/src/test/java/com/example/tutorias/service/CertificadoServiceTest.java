package com.example.tutorias.service;

import com.example.tutorias.entity.Certificado;
import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.Tutor;
import com.example.tutorias.entity.Tutoria;
import com.example.tutorias.repository.CertificadoRepository;
import com.example.tutorias.repository.FeedbackRepository;
import com.example.tutorias.repository.TutorRepository;
import com.example.tutorias.repository.TutoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificadoServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private TutoriaRepository tutoriaRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private CertificadoRepository certificadoRepository;

    private CertificadoService certificadoService;

    @BeforeEach
    void setUp() {
        certificadoService = new CertificadoService(
                tutorRepository,
                tutoriaRepository,
                feedbackRepository,
                certificadoRepository
        );
    }

    @Test
    void generarPdfCertificadoCreaCertificadoConHorasCumplidas() {
        Tutor tutor = tutorAprobado();
        Tutoria tutoria = tutoriaFinalizada(tutor, LocalTime.of(10, 0), LocalTime.of(11, 30));

        when(tutorRepository.findByEmail("tutor@test.com")).thenReturn(Optional.of(tutor));
        when(tutoriaRepository.findByTutorIdAndEstadoOrderByFechaAscHoraInicioAsc(1L, EstadoTutoria.FINALIZADA))
                .thenReturn(List.of(tutoria));
        when(feedbackRepository.obtenerPromedioEstrellasPorTutor(1L)).thenReturn(4.75);
        when(certificadoRepository.save(any(Certificado.class))).thenAnswer(invocation -> {
            Certificado certificado = invocation.getArgument(0);
            certificado.setId(10L);
            return certificado;
        });

        byte[] pdf = certificadoService.generarPdfCertificado("tutor@test.com");

        ArgumentCaptor<Certificado> captor = ArgumentCaptor.forClass(Certificado.class);
        verify(certificadoRepository).save(captor.capture());
        Certificado guardado = captor.getValue();

        assertEquals(2, guardado.getHoras());
        assertEquals(1, guardado.getTotal_tutorias());
        assertEquals(4.75, guardado.getCalificacion_promedio());
        assertEquals("Ada Lovelace", guardado.getNombre_tutor());
        assertTrue(guardado.getCodigo_verificacion() != null && !guardado.getCodigo_verificacion().isBlank());
        assertTrue(new String(pdf, 0, 4, StandardCharsets.US_ASCII).startsWith("%PDF"));
    }

    @Test
    void generarPdfCertificadoRechazaTutorNoAprobado() {
        Tutor tutor = tutorAprobado();
        tutor.setEstadoTutor(false);

        when(tutorRepository.findByEmail("tutor@test.com")).thenReturn(Optional.of(tutor));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> certificadoService.generarPdfCertificado("tutor@test.com"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(certificadoRepository, never()).save(any());
    }

    @Test
    void generarPdfCertificadoRechazaTutorSinTutoriasFinalizadas() {
        Tutor tutor = tutorAprobado();

        when(tutorRepository.findByEmail("tutor@test.com")).thenReturn(Optional.of(tutor));
        when(tutoriaRepository.findByTutorIdAndEstadoOrderByFechaAscHoraInicioAsc(1L, EstadoTutoria.FINALIZADA))
                .thenReturn(List.of());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> certificadoService.generarPdfCertificado("tutor@test.com"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(certificadoRepository, never()).save(any());
    }

    private Tutor tutorAprobado() {
        Tutor tutor = new Tutor();
        tutor.setId(1L);
        tutor.setNombre("Ada");
        tutor.setApellido("Lovelace");
        tutor.setEmail("tutor@test.com");
        tutor.setEstadoTutor(true);
        return tutor;
    }

    private Tutoria tutoriaFinalizada(Tutor tutor, LocalTime horaInicio, LocalTime horaFin) {
        Tutoria tutoria = new Tutoria();
        tutoria.setId(1L);
        tutoria.setNombre("Mentoria finalizada");
        tutoria.setFecha(LocalDate.now().minusDays(1));
        tutoria.setHoraInicio(horaInicio);
        tutoria.setHoraFin(horaFin);
        tutoria.setEstado(EstadoTutoria.FINALIZADA);
        tutoria.setTutor(tutor);
        return tutoria;
    }
}
