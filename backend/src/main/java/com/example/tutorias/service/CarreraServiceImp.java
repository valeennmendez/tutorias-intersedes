package com.example.tutorias.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tutorias.entity.Carrera;
import com.example.tutorias.repository.CarreraRepository;

@Service
public class CarreraServiceImp implements CarreraService {

    @Autowired
    private CarreraRepository carreraRepository;

    @Override
    public List<Carrera> obtenerCarreras() {
        return carreraRepository.findAll();
    }
}