package com.example.tutorias.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    @Autowired
    private JavaMailSender mailSender;

    // Inyectamos el remitente desde el archivo de propiedades para no dejarlo hardcodeado
    @Value("${spring.mail.username}")
    private String remitente;

    public void enviarNotificacionAprobacion(String emailDestino, String nombreMateria) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        
        mensaje.setFrom(remitente);
        mensaje.setTo(emailDestino);
        mensaje.setSubject("¡Felicitaciones! Tu postulación fue aprobada");
        mensaje.setText("Hola,\n\nTe informamos que tu postulación para ser tutor de la materia '" 
                + nombreMateria + "' ha sido aprobada.\n\n"
                + "A partir de este momento, ya puedes acceder a tus funciones extendidas en la plataforma.\n\n"
                + "Atentamente,\nSistema de Tutorías Universitarias UNNOBA.");

        mailSender.send(mensaje);
    }

    public void enviarNotificacionRechazo(String emailDestino, String nombreMateria, String motivo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        
        mensaje.setFrom(remitente);
        mensaje.setTo(emailDestino);
        mensaje.setSubject("Actualización sobre tu postulación a tutor");
        mensaje.setText("Hola,\n\nLamentamos informarte que tu postulación para dictar tutorías en la materia '" 
                + nombreMateria + "' no pudo ser aprobada en esta ocasión.\n\n"
                + "Motivo de la revisión: " + motivo + "\n\n"
                + "Puedes realizar una nueva postulación corrigiendo los puntos indicados si lo deseas.\n\n"
                + "Atentamente,\nSistema de Tutorías Universitarias UNNOBA.");

        mailSender.send(mensaje);
    }
}