package com.example.tutorias.service;

import com.example.tutorias.repository.TutorRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tutorias.entity.Alumno;
import com.example.tutorias.entity.Materia;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.Persona;
import com.example.tutorias.entity.PostulacionTutor;
import com.example.tutorias.entity.PostulacionTutorEstado;
import com.example.tutorias.entity.Role;
import com.example.tutorias.entity.Tutor;
import com.example.tutorias.exception.ReglaNegocioException;
import com.example.tutorias.repository.AlumnoRepository;
import com.example.tutorias.repository.MateriaRepository;
import com.example.tutorias.repository.PersonaRepository;
import com.example.tutorias.repository.PostulacionTutorRepository;
import com.example.tutorias.dto.postulaciones.PostulacionTutorResponseDTO;
import java.time.LocalDateTime;
import com.example.tutorias.util.NotificacionService;

@Service
public class PostulacionTutorServiceImp implements PostulacionTutorService {
    
    private final TutorRepository tutorRepository;

	@Autowired
    private AlumnoRepository alumnoRepository;
    
    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PostulacionTutorRepository postulacionTutorRepository;

    @Autowired
    private NotificacionService notificacionService;

    @PersistenceContext
    private EntityManager entityManager;

	 PostulacionTutorServiceImp(TutorRepository tutorRepository) {
		this.tutorRepository = tutorRepository;
	 }

    @Override
    @Transactional
    public PostulacionTutorResponseDTO registrarPostulacion(Long alumnoId, Long materiaId, Double notaAprobacion, String justificacion, String sedePreferencia, ModalidadTutoria modalidad, MultipartFile archivoPdf) {
        
        if(justificacion == null || justificacion.trim().isEmpty()) {
            throw new ReglaNegocioException("La justificación no puede estar vacía.");
        }

        if(notaAprobacion == null || notaAprobacion < 4.0 || notaAprobacion > 10.0) {
            throw new ReglaNegocioException("La nota de aprobación debe estar entre 4.0 y 10.0.");
        }

        if(sedePreferencia == null || sedePreferencia.trim().isEmpty()) {
            throw new ReglaNegocioException("Debe indicar una sede de preferencia.");
        }

        if(modalidad == null) {
            throw new ReglaNegocioException("Debe indicar una modalidad de tutoría.");
        }

        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new ReglaNegocioException("El alumno especificado no existe."));

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new ReglaNegocioException("La materia especificada no existe."));

        List<PostulacionTutor> postulacionesExistentes = postulacionTutorRepository.findByPostulanteId(alumnoId);
        boolean yaSePostulo = postulacionesExistentes.stream()
                .anyMatch(p -> p.getMateria().getId().equals(materiaId) && p.getEstado() == PostulacionTutorEstado.PENDIENTE);

        if (yaSePostulo) {
            throw new ReglaNegocioException("Ya has postulado para esta materia y sigue pendiente.");
        }

        String rutaArchivo = fileStorageService.guardarArchivo(archivoPdf);

        PostulacionTutor nuevaPostulacion = new PostulacionTutor();
        nuevaPostulacion.setPostulante(alumno);
        nuevaPostulacion.setMateria(materia);
        nuevaPostulacion.setJustificacion(justificacion);
        nuevaPostulacion.setNotaAprobacion(notaAprobacion);
        nuevaPostulacion.setPdfPath(rutaArchivo);
        nuevaPostulacion.setEstado(PostulacionTutorEstado.PENDIENTE);
        nuevaPostulacion.setSedePreferencia(sedePreferencia);
        nuevaPostulacion.setModalidadPreferencia(modalidad);

        PostulacionTutor savedPostulacion = postulacionTutorRepository.save(nuevaPostulacion);
        
        return PostulacionTutorResponseDTO.from(savedPostulacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulacionTutorResponseDTO> obtenerPostulacionesPorAlumno(Long alumnoId) {
        return postulacionTutorRepository.findByPostulanteId(alumnoId).stream()
                .map(PostulacionTutorResponseDTO::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulacionTutorResponseDTO> obtenerTodasPostulaciones() {
        return postulacionTutorRepository.findAll().stream()
                .sorted(Comparator.comparing(PostulacionTutor::getCreatedAt).reversed())
                .map(PostulacionTutorResponseDTO::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String obtenerRutaPdfPostulacion(Long postulacionId) {
        PostulacionTutor postulacion = postulacionTutorRepository.findById(postulacionId)
                .orElseThrow(() -> new ReglaNegocioException("La postulación no existe."));
        return postulacion.getPdfPath();
    }

    @Override
    @Transactional
    public void actualizarEstadoPostulacion(Long postulacionId, PostulacionTutorEstado nuevoEstado, Long adminId, String adminComentario) {
        PostulacionTutor postulacion = postulacionTutorRepository.findById(postulacionId)
                .orElseThrow(() -> new ReglaNegocioException("La postulación no existe."));

        if (adminId != null) {
            Persona administrador = personaRepository.findById(adminId)
                    .orElseThrow(() -> new ReglaNegocioException("El administrador no existe."));
            postulacion.setRevisor(administrador);
        }
        //actualizamos datos de la postulación
        postulacion.setEstado(nuevoEstado);
        postulacion.setAdminComentario(adminComentario);
        postulacion.setReviewedAt(LocalDateTime.now());
        postulacionTutorRepository.save(postulacion);

        System.out.println("🚩ATENCIÓN: El estado que llegó al Service es: " + nuevoEstado);

        //si la postulación es aprobada, promovemos al alumno a tutor y le asignamos la materia
        if (PostulacionTutorEstado.APROBADA.equals(nuevoEstado)) {
            Alumno alumnoParaPromover = postulacion.getPostulante();
            Materia materia = postulacion.getMateria();
            
            //tambien guardamos datos para el mail   antes del clear 
            String emailUsuario = alumnoParaPromover.getEmail();
            String nombreMateria = materia.getNombre();

            // Le cambiamos el rol al objeto en memoria para que, si Hibernate intenta 
            // sobreescribir la base de datos después, lo haga con el valor correcto.
            alumnoParaPromover.setRole(Role.TUTOR); 

            boolean yaEsTutor = tutorRepository.findById(alumnoParaPromover.getId()).isPresent();

            if (!yaEsTutor) {
                entityManager.createNativeQuery( //crea un registro vacío en la tabla tutor para un alumno que se está promoviendo a tutor, y además es una FK verifica que exista el mismo id en la table persona.
                "INSERT INTO tutor (id, administrador_id) VALUES (:id, :adminId)"
                )
                .setParameter("id", alumnoParaPromover.getId()) //vamos a tener el mismo id en la tabla persona, alumno y tutor, por la herencia y cómo resuelve el triple join
                .setParameter("adminId", adminId)
                .executeUpdate();

                entityManager.createNativeQuery(
                    "UPDATE persona SET dtype = 'Tutor' WHERE id = :id"
                )
                .setParameter("id", alumnoParaPromover.getId())
                .executeUpdate();

                entityManager.flush();  //agregamos flush para asegurarnos de que los cambios se escriban en la base de datos antes de continuar
                entityManager.clear();
            }
            //recuperamos el tutor recién creado o existente, porque ya actualizamos el dtype a 'Tutor' y ahora podemos buscarlo como tal
            Tutor tutor = tutorRepository.findById(alumnoParaPromover.getId())
                    .orElseThrow(() -> new ReglaNegocioException("Error al crear el tutor."));

            // Como la 'materia' quedó desconectada por el clear(), 
            // usamos merge() para volver a atarla a la sesión activa antes de guardarla.
            Materia materiaActiva = entityManager.merge(materia);

            //pequeña validación para evitar duplicados en la relación ManyToMany entre tutor y materia
            if (!tutor.getMaterias().contains(materiaActiva)) {
            tutor.getMaterias().add(materiaActiva);
            tutorRepository.save(tutor);
            // Enviamos la notificación de éxito
            notificacionService.enviarNotificacionAprobacion(emailUsuario, nombreMateria);
        }
        } else if (PostulacionTutorEstado.RECHAZADA.equals(nuevoEstado)) { //mejoramos el matcheo
            Alumno alumnoParaRechazo = postulacion.getPostulante();
            Materia materiaParaRechazo = postulacion.getMateria();

            System.out.println("🚩 ¡ENTRÓ AL BLOQUE DE RECHAZO!");
            System.out.println("🚩 Intentando mandar mail a: " + alumnoParaRechazo.getEmail());

            String motivo = adminComentario != null ? adminComentario : "No cumples con los requisitos actuales.";
            notificacionService.enviarNotificacionRechazo(alumnoParaRechazo.getEmail(), materiaParaRechazo.getNombre(), motivo);

            System.out.println("🚩 ¡MAIL ENVIADO AL SERVIDOR DE GOOGLE!");
        }
    }

    @Override
    @Transactional
    public void eliminarPostulacion(Long postulacionId, Long alumnoId) {
        PostulacionTutor postulacion = postulacionTutorRepository.findById(postulacionId)
                .orElseThrow(() -> new ReglaNegocioException("La postulación no existe."));

        if (!postulacion.getPostulante().getId().equals(alumnoId)) {
            throw new ReglaNegocioException("No tienes permiso para eliminar esta postulación porque no te pertenece.");
        }

        postulacionTutorRepository.delete(postulacion);
    }
}