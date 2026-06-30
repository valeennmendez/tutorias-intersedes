package com.example.tutorias.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()    
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/create-admin").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/create-admin/").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/verify-otp").permitAll()
                .requestMatchers("/error").permitAll() // Permitir acceso a la página de error sin autenticación
                .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()

                .requestMatchers(HttpMethod.GET, "/materias/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/materias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/materias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/materias/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/tutorias/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/tutorias/**").hasAnyRole("TUTOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/tutorias/**").hasAnyRole("TUTOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/tutorias/**").hasAnyRole("TUTOR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/tutorias/{id}/agregar-link").hasAnyRole("TUTOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/tutorias/{id}/actualizar-link").hasAnyRole("TUTOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/tutorias/{id}/eliminar-link").hasAnyRole("TUTOR", "ADMIN")

                .requestMatchers("/inscripciones/**").authenticated()
                .requestMatchers("/feedback/**").hasAnyRole("ALUMNO", "ADMIN")
                .requestMatchers("/avisos/**").hasAnyRole("TUTOR", "ADMIN", "ALUMNO")
                .requestMatchers("/certificados/**").hasAnyRole("TUTOR", "ADMIN")
                .requestMatchers("/solicitudes-tutor/**").authenticated()
                .requestMatchers("/alumnos/**").hasAnyRole("ALUMNO", "ADMIN")
                .requestMatchers("/tutores/**").hasAnyRole("TUTOR", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/feedback/**").authenticated()

                .anyRequest().authenticated()
            )

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint())
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Acceso denegado\", \"detalle\": \"" + authException.getMessage() + "\"}");
        };
    }
    
    @Bean
    public RoleHierarchy roleHierarchy() {
        // La nueva forma de Spring Security 6+ usa un método estático (factory method)
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_TUTOR \n ROLE_TUTOR > ROLE_ALUMNO");
    }
}