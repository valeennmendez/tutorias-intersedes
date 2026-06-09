package com.example.tutorias.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.tutorias.entity.Persona;
import com.example.tutorias.repository.PersonaRepository;



@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonaRepository personaRepository;

    public CustomUserDetailsService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    //No deja editar la interfaz UserDetailsService, pero el método loadUserByUsername busca por username, en nuestro caso el email es único y se usa para login, así que lo renombramos a email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Persona> persona = personaRepository.findByEmail(email); //renombramos para que busqeu por email

        if (persona.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado con el email: " + email);
        }

        return new UserDetailsImpl(persona.get());
    }
}
