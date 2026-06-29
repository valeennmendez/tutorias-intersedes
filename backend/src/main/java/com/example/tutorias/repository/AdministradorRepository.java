package com.example.tutorias.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutorias.entity.Administrador;

public interface AdministradorRepository extends JpaRepository<Administrador, Long>{
    
}
