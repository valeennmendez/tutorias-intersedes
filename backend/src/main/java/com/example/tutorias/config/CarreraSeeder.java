package com.example.tutorias.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.tutorias.entity.Carrera;
import com.example.tutorias.repository.CarreraRepository;

@Component
public class CarreraSeeder implements ApplicationRunner {

    @Autowired
    private CarreraRepository carreraRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (carreraRepository.count() > 0) return;

        List<String> carreras = List.of(
            "Agronomía",
            "Ingeniería Agronómica",
            "Tecnicatura en Mecanización de la Producción Agropecuaria",
            "Tecnicatura en Producción Agropecuaria",
            "Licenciatura en Ciencias de los Alimentos",
            "Ingeniería en Alimentos",
            "Tecnicatura en Producción de Alimentos",
            "Licenciatura en Genética",
            "Enfermería",
            "Licenciatura en Enfermería",
            "Contador Público",
            "Licenciatura en Administración",
            "Tecnicatura en Gestión de PYMES",
            "Tecnicatura en Gestión Pública",
            "Abogacía",
            "Martillero y Corredor Público",
            "Licenciatura en Diseño de Indumentaria y Textil",
            "Tecnicatura en Diseño de Indumentaria y Textil",
            "Licenciatura en Diseño Gráfico",
            "Tecnicatura en Diseño Gráfico",
            "Licenciatura en Diseño Industrial",
            "Tecnicatura en Diseño Industrial",
            "Ingeniería Industrial",
            "Tecnicatura en Mantenimiento Industrial",
            "Tecnicatura en Ferrocarriles",
            "Ingeniería Mecánica",
            "Tecnicatura en Mecánica",
            "Licenciatura en Sistemas",
            "Programador Universitario",
            "Analista de Sistemas",
            "Ingeniería en Informática",
            "Tecnicatura en Informática Agropecuaria",
            "Tecnicatura Universitaria en Soporte Informático",
            "Técnico Universitario en Desarrollo de Sistemas Informáticos"
        );

        carreras.forEach(nombre -> {
            Carrera carrera = new Carrera();
            carrera.setNombre(nombre);
            carreraRepository.save(carrera);
        });

        System.out.println("Seed de carreras ejecutada: " + carreras.size() + " carreras insertadas.");
    }
}