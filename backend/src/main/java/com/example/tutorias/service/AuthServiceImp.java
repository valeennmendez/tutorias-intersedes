package com.example.tutorias.service;

import com.example.tutorias.dto.auth.LoginRequestDTO;
import com.example.tutorias.dto.auth.RegistroRequestDTO;
import com.example.tutorias.dto.auth.UsuarioResponseDTO;
import com.example.tutorias.entity.Alumno;
import com.example.tutorias.entity.Carrera;
import com.example.tutorias.entity.Persona;
import com.example.tutorias.entity.Role;
import com.example.tutorias.exception.ReglaNegocioException;
import com.example.tutorias.repository.AlumnoRepository;
import com.example.tutorias.repository.CarreraRepository;
import com.example.tutorias.repository.PersonaRepository;
import com.example.tutorias.util.JwtTokenUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthServiceImp implements AuthService {

    private final AlumnoRepository alumnoRepository; 
    private final AuthenticationManager authenticationManager; 
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder; 
    private final JwtTokenUtil jwtTokenUtil;
    private final CarreraRepository carreraRepository;

    public AuthServiceImp(AlumnoRepository alumnoRepository, 
                          AuthenticationManager authenticationManager,
                          PersonaRepository personaRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtTokenUtil jwtTokenUtil,
                          CarreraRepository carreraRepository) {
        this.alumnoRepository = alumnoRepository;
        this.authenticationManager = authenticationManager;
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.carreraRepository = carreraRepository;
    }

    @Override
    public void registerUser(RegistroRequestDTO registroRequest) {

        //Limpiamos el email para evitar problemas de espacios o mayúsculas
        String emailLimpio = registroRequest.getEmail().trim().toLowerCase();

        //validaciones
        if (personaRepository.existsByEmail(emailLimpio)) {
            throw new ReglaNegocioException("El email ya se encuentra registrado.");
        }

        if (alumnoRepository.existsByLegajo(registroRequest.getLegajo())) {
            throw new ReglaNegocioException("El legajo ya se encuentra registrado.");
        }

        if (alumnoRepository.existsByDni(registroRequest.getDni())) {
            throw new ReglaNegocioException("El DNI ya se encuentra registrado.");
        }

        //Búsqueda segura de relaciones (Carrera)
        Carrera carrera = carreraRepository.findByNombre(registroRequest.getCarrera().trim())
            .orElseThrow(() -> new RuntimeException("La carrera especificada no existe en el sistema."));
        
        // mapeamos el dto a la entidad
        Alumno nuevoAlumno = new Alumno();
        nuevoAlumno.setNombre(registroRequest.getNombre().trim());
        nuevoAlumno.setApellido(registroRequest.getApellido().trim());
        nuevoAlumno.setEmail(emailLimpio);
        nuevoAlumno.setCarrera(carrera);
        nuevoAlumno.setLegajo(registroRequest.getLegajo().trim());
        nuevoAlumno.setAnioInicio(registroRequest.getAnioInicio());

        //mapeamos los datos heredados de Persona
        nuevoAlumno.setDni(registroRequest.getDni().trim());
        nuevoAlumno.setFechanacimiento(registroRequest.getFechanacimiento());
        nuevoAlumno.setDireccion(registroRequest.getDireccion().trim());

        //Encriptamos la contraseña antes de guardar
        nuevoAlumno.setPassword(passwordEncoder.encode(registroRequest.getPassword().trim()));
        nuevoAlumno.setRole(Role.ALUMNO);
        nuevoAlumno.setEstado(true); // Por defecto, el nuevo alumno queda activo. Se puede cambiar según la lógica de negocio (ej: requerir verificación por email).
        // perisstimos
        alumnoRepository.save(nuevoAlumno);
    }

    @Override
    public String loginUser(LoginRequestDTO loginRequest) {
        // Spring Security se encarga de autenticar al usuario usando el AuthenticationManager, que a su vez usa el CustomUserDetailsService para cargar el usuario (con email) y verificar la contraseña
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail().trim().toLowerCase(),
                loginRequest.getPassword()
            )
        );

        String role = authentication.getAuthorities().stream()
            .findFirst()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .orElseThrow(() -> new RuntimeException("Usuario autenticado sin rol asignado."));

        return jwtTokenUtil.generateToken(loginRequest.getEmail(), role); 
    }

    public UsuarioResponseDTO getUsuarioById(Long id) {
        Persona persona = personaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado en el sistema."));
        
        return new UsuarioResponseDTO(
            persona.getId(),
            persona.getNombre(),
            persona.getApellido(),
            persona.getEmail(),
            persona.getRole()
        );
    }
    
}
