package com.example.tutorias.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.tutorias.entity.Persona;

import org.springframework.security.core.userdetails.UserDetails;


public class UserDetailsImpl implements UserDetails {

    private final Persona persona;

    public UserDetailsImpl(Persona persona) {
        this.persona = persona;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
            new SimpleGrantedAuthority("ROLE_" + persona.getRole())
        );
    }

    @Override
    public String getUsername() {
        return persona.getEmail(); // Usamos el email como username, se ve que la interfaz UserDetailsService busca por username pero en nuestro caso el email es único y se usa para login
    }

    @Override
    public String getPassword() {
        return persona.getPassword();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(persona.getActivo());
    }

}

