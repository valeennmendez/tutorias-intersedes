package com.example.tutorias.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tutorias.entity.Carrera;
import com.example.tutorias.service.CarreraService;

@RestController
@RequestMapping("/carreras")
public class CarreraController {

    @Autowired
    private CarreraService carreraService;

    @GetMapping
    public ResponseEntity<List<Carrera>> obtenerCarreras() {
        return ResponseEntity.ok(carreraService.obtenerCarreras());
    }
}