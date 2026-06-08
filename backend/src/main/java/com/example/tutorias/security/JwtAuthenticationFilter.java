package com.example.tutorias.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.tutorias.util.JwtTokenUtil;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    
    public JwtAuthenticationFilter(
            JwtTokenUtil jwtTokenUtil,
            UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain)
                throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
        }

        String path = request.getRequestURI();

        if (path.equals("/auth/login")
                || path.equals("/auth/register")
                || path.startsWith("/pagos/webhook")
                || path.startsWith("/pagos/mercadoPago")
                || path.startsWith("/webhooks/")) {

                filterChain.doFilter(request, response);
                return;
        }



        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

        String token = authHeader.substring(7); 
        String email = jwtTokenUtil.getSubject(token);

        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null &&
                jwtTokenUtil.verify(token)) {

                var userDetails =
                        userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
        }
        }



        filterChain.doFilter(request, response);
        }
}