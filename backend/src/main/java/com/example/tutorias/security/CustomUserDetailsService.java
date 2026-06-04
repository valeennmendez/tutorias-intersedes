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

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Optional<Persona> persona = personaRepository.findByUsername(username);

        if (persona.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return (UserDetails) new UserDetailsImpl(persona.get());
    }
}
