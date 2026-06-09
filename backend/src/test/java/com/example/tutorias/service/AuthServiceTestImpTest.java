package com.example.tutorias.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.tutorias.dto.auth.LoginRequestDTO;
import com.example.tutorias.dto.auth.RegistroRequestDTO;
import com.example.tutorias.dto.auth.UsuarioResponseDTO;
import com.example.tutorias.entity.Alumno;
import com.example.tutorias.entity.Carrera;
import com.example.tutorias.entity.Persona;
import com.example.tutorias.entity.Role;
import com.example.tutorias.repository.AlumnoRepository;
import com.example.tutorias.repository.CarreraRepository;
import com.example.tutorias.repository.PersonaRepository;
import com.example.tutorias.util.JwtTokenUtil;



@ExtendWith(MockitoExtension.class)
public class AuthServiceTestImpTest {
    
    @Mock
    private AlumnoRepository alumnoRepository;
    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private CarreraRepository carreraRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    
    @InjectMocks
    private AuthServiceImp authServiceImp;

    // Método auxiliar para crear un DTO válido para nuestros tests
    private RegistroRequestDTO crearDtoValido() {
        RegistroRequestDTO dto = new RegistroRequestDTO();
        dto.setNombre("Charly");
        dto.setApellido("García");
        dto.setEmail("charly@comunidad.unnoba.edu.ar");
        dto.setDni("12345678"); // Agregado para que no explote el trim()
        dto.setLegajo("12345");
        dto.setAnioInicio(2023);
        dto.setPassword("clave123");
        dto.setFechanacimiento(LocalDate.of(1951, 10, 23));
        dto.setDireccion("Calle Falsa 123");
        dto.setCarrera("Sistemas");
        return dto;
    }

    @Test
    void registerUser_DatosValidos_DeberiaGuardarAlumnoCorrectamente() {
        RegistroRequestDTO dto = crearDtoValido();
        
        Carrera carreraFalsa = new Carrera("Sistemas");

        when(personaRepository.existsByEmail(anyString())).thenReturn(false);
        when(alumnoRepository.existsByLegajo(anyString())).thenReturn(false);
        when(alumnoRepository.existsByDni(anyString())).thenReturn(false); 
        when(carreraRepository.findByNombre("Sistemas")).thenReturn(Optional.of(carreraFalsa));
        when(passwordEncoder.encode("clave123")).thenReturn("clave_encriptada");

        authServiceImp.registerUser(dto);
        
        ArgumentCaptor<Alumno> alumnoCaptor = ArgumentCaptor.forClass(Alumno.class);
        verify(alumnoRepository).save(alumnoCaptor.capture());

        Alumno guardado = alumnoCaptor.getValue();
        assertEquals("Charly", guardado.getNombre()); 
        assertEquals("charly@comunidad.unnoba.edu.ar", guardado.getEmail()); 
        assertEquals("12345678", guardado.getDni());
        assertEquals("12345", guardado.getLegajo());
        assertEquals("clave_encriptada", guardado.getPassword());
        assertEquals(Role.ALUMNO, guardado.getRole());
    }

    @Test
    void registerUser_EmailYaExiste_DeberiaLanzarExcepcion() {
        RegistroRequestDTO dto = crearDtoValido();
        
        // Hacemos que el mock simule que el email ya existe
        when(personaRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authServiceImp.registerUser(dto);
        });
        
        assertEquals("El email ya se encuentra registrado.", exception.getMessage());
        verify(alumnoRepository, never()).save(any());
    }

    @Test
    void registerUser_LegajoYaExiste_DeberiaLanzarExcepcion() {
        RegistroRequestDTO dto = crearDtoValido();
        
        when(personaRepository.existsByEmail(anyString())).thenReturn(false);
        when(alumnoRepository.existsByLegajo(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authServiceImp.registerUser(dto);
        });
        assertEquals("El legajo ya se encuentra registrado.", exception.getMessage());
        verify(alumnoRepository, never()).save(any());
    }

    @Test
    void registerUser_CarreraNoExiste_DeberiaLanzarExcepcion() {
        RegistroRequestDTO dto = crearDtoValido();
        dto.setCarrera("CarreraFantasma"); // Modificamos solo lo que queremos romper
        
        when(personaRepository.existsByEmail(anyString())).thenReturn(false);
        when(alumnoRepository.existsByLegajo(anyString())).thenReturn(false);
        when(carreraRepository.findByNombre("CarreraFantasma")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authServiceImp.registerUser(dto);
        });
        
        assertEquals("La carrera especificada no existe en el sistema.", exception.getMessage());
        verify(alumnoRepository, never()).save(any());
    }

    @Test
    void loginUser_CredencialesValidas_DeberiaRetornarToken() {
        // simular el DTO de login
        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail("charly@comunidad.unnoba.edu.ar");
        loginDto.setPassword("clave123");

        // Creamos mocks para simular la sesión de Spring Security
        Authentication authenticationMock = mock(Authentication.class);
        GrantedAuthority authorityMock = mock(GrantedAuthority.class);
        
        // Le decimos al mock de autoridad que devuelva el rol
        when(authorityMock.getAuthority()).thenReturn("ROLE_ALUMNO");
        // Le decimos al mock de autenticación que devuelva una lista con ese rol
        doReturn(List.of(authorityMock)).when(authenticationMock).getAuthorities();
        // Cuando el manager intente autenticar, le devolvemos nuestro authenticationMock exitoso
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authenticationMock);
            
        // Cuando pida generar el token, devolvemos uno de prueba
        when(jwtTokenUtil.generateToken("charly@comunidad.unnoba.edu.ar", "ROLE_ALUMNO"))
            .thenReturn("eyJhbGciOiJIUzI1NiJ9.token.falso");

        // 2. ACT
        String tokenGenerado = authServiceImp.loginUser(loginDto);

        // 3. ASSERT
        assertNotNull(tokenGenerado);
        assertEquals("eyJhbGciOiJIUzI1NiJ9.token.falso", tokenGenerado);
        
        // Verificamos que efectivamente se llamó al manager para autenticar
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void loginUser_UsuarioSinRol_DeberiaLanzarExcepcion() {
        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail("charly@comunidad.unnoba.edu.ar");
        loginDto.setPassword("clave123");

        Authentication authenticationMock = mock(Authentication.class);
        doReturn(java.util.Collections.emptyList()).when(authenticationMock).getAuthorities();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authenticationMock);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authServiceImp.loginUser(loginDto);
        });

        assertEquals("Usuario autenticado sin rol asignado.", exception.getMessage());
        // Verificamos que NUNCA llegó a pedir el token
        verify(jwtTokenUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void loginUser_CredencialesInvalidas_DeberiaLanzarExcepcion() {
        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail("charly@comunidad.unnoba.edu.ar");
        loginDto.setPassword("claveIncorrecta");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Credenciales inválidas"));
        
        assertThrows(BadCredentialsException.class, () -> {
            authServiceImp.loginUser(loginDto);
        });
        
        // Verificamos que el token nunca se intentó generar
        verify(jwtTokenUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void loginUser_ErrorGenerandoToken_DeberiaLanzarExcepcion() {
        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail("charly@comunidad.unnoba.edu.ar");
        loginDto.setPassword("clave123");

        Authentication authenticationMock = mock(Authentication.class);
        GrantedAuthority authorityMock = mock(GrantedAuthority.class);
        
        //Acá simulamos que el usuario se autentica correctamente pero que ocurre un error al generar el token (por ejemplo, un problema con la clave secreta)
        when(authorityMock.getAuthority()).thenReturn("ROLE_ALUMNO");
        doReturn(List.of(authorityMock)).when(authenticationMock).getAuthorities();

        // Simulamos autenticación exitosa
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authenticationMock);

        //acá lo rompemos
        when(jwtTokenUtil.generateToken(anyString(), anyString()))
            .thenThrow(new RuntimeException("Error generando token"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authServiceImp.loginUser(loginDto);
        });

        assertEquals("Error generando token", exception.getMessage());
    }

    @Test
    void getUsuarioById_UsuarioExiste_DeberiaRetornarDTO() {
        Long idBuscado = 1L;
        Persona alumnoEncontrado = new Alumno();
        alumnoEncontrado.setId(idBuscado);
        alumnoEncontrado.setNombre("Charly");
        alumnoEncontrado.setApellido("García");
        alumnoEncontrado.setEmail("charly@comunidad.unnoba.edu.ar");
        alumnoEncontrado.setRole(Role.ALUMNO);

        when(personaRepository.findById(idBuscado)).thenReturn(java.util.Optional.of(alumnoEncontrado));

        UsuarioResponseDTO resultado = authServiceImp.getUsuarioById(idBuscado);

        assertNotNull(resultado);
        assertEquals(idBuscado, resultado.getId());
        assertEquals("Charly", resultado.getNombre());
        assertEquals("García", resultado.getApellido());
        assertEquals("charly@comunidad.unnoba.edu.ar", resultado.getEmail());
        assertEquals("ALUMNO", resultado.getRole());

        verify(personaRepository).findById(idBuscado);
    }

    @Test
    void getUsuarioById_UsuarioNoExiste_DeberiaLanzarExcepcion() {
        Long idInexistente = 999L;

        when(personaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authServiceImp.getUsuarioById(idInexistente);
        });

        assertEquals("Usuario no encontrado en el sistema.", exception.getMessage());
        verify(personaRepository).findById(idInexistente);
    }
}
