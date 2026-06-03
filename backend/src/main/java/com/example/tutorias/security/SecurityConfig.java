package com.example.tutorias.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.userdetails.UserDetailsService;



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
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
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
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


   @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        
        //Lo deje para que despues sigamos la misma estructura

        http
            .csrf(csrf -> csrf.disable())
             .cors(cors -> {})
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth

                
                //AUTENTICACIÓN / REGISTRO
                // =========================
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                .requestMatchers(
                    "/alumnos/register", "/alumnos/auth",
                    "/docentes/register", "/docentes/auth"
                ).permitAll()

                
                // CURSOS
                // =========================
                .requestMatchers(HttpMethod.GET, "/cursos/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/cursos/**").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.PUT,  "/cursos/**").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.DELETE, "/cursos/**").hasAnyRole("ADMIN", "DOCENTE")

                
                // ALUMNOS
                // =========================
                .requestMatchers(HttpMethod.POST, "/alumnos/create").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.PUT, "/alumnos/**").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.DELETE, "/alumnos/**").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.GET, "/alumnos").authenticated()


                .requestMatchers(HttpMethod.GET, "/alumnos/**").hasAnyRole("ADMIN", "DOCENTE")

                                
                // DOCENTES
                // =========================
               
                .requestMatchers(HttpMethod.DELETE, "/docentes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/docentes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/docentes/create/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/docentes").authenticated()
                .requestMatchers(HttpMethod.GET, "/docentes/**").hasAnyRole("ADMIN", "DOCENTE")

            
                // ASISTENCIAS
                // =========================
                .requestMatchers(HttpMethod.GET, "/asistencias/**")
                    .hasAnyRole("ADMIN", "DOCENTE", "ALUMNO")
                .requestMatchers(HttpMethod.POST, "/asistencias/**")
                    .hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.PUT, "/asistencias/**")
                    .hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.DELETE, "/asistencias/**")
                    .hasAnyRole("ADMIN", "DOCENTE")

                // FILES
                // =========================
                .requestMatchers(HttpMethod.POST, "/files/upload")
                    .hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.GET, "/files/download/**")
                    .authenticated()

                
                //EXÁMENES
                // =========================
                .requestMatchers(HttpMethod.GET, "/examenes/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/examenes/**").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.PUT,  "/examenes/**").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.DELETE, "/examenes/**").hasAnyRole("ADMIN", "DOCENTE")
                

                
                // ENTREGAS
                // =========================
                .requestMatchers(HttpMethod.GET, "/entregas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/entregas/alumno/**").hasAnyRole("ALUMNO", "DOCENTE", "ADMIN")
                .requestMatchers(HttpMethod.POST,"/entregas/examen/*/alumno/*").hasRole("ALUMNO")
                .requestMatchers(HttpMethod.POST, "/entregas/**").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.PUT,  "/entregas/**").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.DELETE, "/entregas/**").hasAnyRole("ADMIN", "DOCENTE")


                // MERCADO PAGO
                // =========================
                .requestMatchers(HttpMethod.POST, "/pagos/mercadoPago").permitAll()
                .requestMatchers(HttpMethod.POST, "/pagos/webhook").permitAll()
                .requestMatchers(HttpMethod.GET, "/pagos/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/pagos/crear").hasAnyRole("ADMIN")
                .requestMatchers("/webhooks/**").permitAll()
                .requestMatchers("/auth/**").permitAll()                            
                


                
                // ADMIN
                // =========================
                .requestMatchers("/admin/**").hasRole("ADMIN")

                
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

} 
