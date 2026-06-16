package com.example.tutorias.config;

import com.example.tutorias.entity.Materia;
import com.example.tutorias.repository.MateriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MateriaRepository materiaRepository;

    public DataInitializer(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    @Override
    public void run(String... args) {
        List<String> materias = List.of(
                "Elementos de Matemática",
                "Introducción a la Programación Imperativa",
                "Sistemas y Organizaciones",
                "Organización y Arquitectura de Computadoras",
                "Programación Imperativa",
                "Análisis Matemático I",
                "Sistemas de Información",
                "Álgebra y Geometría Analítica",
                "Aspectos Sociales e Institucionales de la Universidad",
                "Lengua Extranjera",
                "Análisis Matemático II",
                "Ingeniería de Requisitos",
                "Estructura de Datos",
                "Proceso de Software",
                "Introducción a la Programación Orientada a Objetos",
                "Base de Datos I",
                "Matemática Discreta",
                "Redes de Datos",
                "Base de Datos II",
                "Diseño de Sistemas",
                "Programación Orientada a Objetos",
                "Probabilidades y Estadística",
                "Sistemas Operativos I",
                "Base de Datos III",
                "Diseño de Interfaz",
                "Programación Multiplataforma",
                "Programación Lógica y Funcional",
                "Sistemas Operativos II",
                "Programación Distribuida, Concurrente y Paralela",
                "Economía y Organizaciones",
                "Calidad y Testing",
                "Optativa I",
                "Práctica Profesional y Legislación",
                "Optativa II",
                "Gestión de Proyectos",
                "Ciencias de la Computación",
                "Introducción a la Inteligencia Computacional",
                "Formulación y Evaluación de Proyectos",
                "Lenguajes y Compiladores",
                "Ciencia de Datos",
                "Trabajo Final",
                "Química General e Inorgánica",
                "Realidad Agropecuaria",
                "Elementos de Álgebra",
                "Introducción a la Biología",
                "Elementos de Análisis Matemático",
                "Química Orgánica",
                "Química Biológica",
                "Botánica I",
                "Estadística",
                "Física General",
                "Diseño de Experimentos",
                "Anatomía y Fisiología Animal",
                "Botánica II",
                "Microbiología Agrícola y Bioinsumos",
                "Genética",
                "Agroclimatología",
                "Edafología",
                "Mejoramiento Genético",
                "Agroecología",
                "Fisiología Vegetal",
                "Nutrición Animal",
                "Fertilidad y Uso de Fertilizantes",
                "Economía General y del Sistema Agroalimentario",
                "Riego y Drenaje",
                "Maquinaria y Tecnología de Uso Agrícola",
                "Ecofisiología Vegetal",
                "Producción de Forrajes y Pasturas",
                "Fitopatología",
                "Zoología Agrícola",
                "Conservación y Planificación del Uso de la Tierra",
                "Malezas",
                "Producción Porcina y Aviar",
                "Producción de Oleaginosas",
                "Producción Bovina",
                "Producción de Semillas",
                "Gestión de Agronegocios",
                "Geomática Aplicada a la Agronomía",
                "Producción de Cereales",
                "Horticultura",
                "Actividades Formativas Académicas",
                "Actividades Formativas Prácticas",
                "Taller de Diseño Gráfico I",
                "Sistemas de Representación I",
                "Tecnología y Producción de Diseño Gráfico I",
                "Geometría Elemental",
                "Tecnología y Producción de Diseño Gráfico II",
                "Taller de Diseño Gráfico II",
                "Historia del Diseño",
                "Sistemas de Representación II",
                "Taller de Diseño Gráfico III",
                "Morfología I",
                "Historia del Diseño Gráfico",
                "Sistema de Representación para Diseño Gráfico",
                "Desarrollo Web",
                "Narrativa Audiovisual I",
                "Taller de Diseño Gráfico IV",
                "Morfología II",
                "Comunicación I",
                "Tipografía I",
                "Taller de Diseño Gráfico V",
                "Comunicación II",
                "Tipografía II",
                "Narrativa Audiovisual II",
                "Taller de Diseño Gráfico VI",
                "Laboratorio de Tecnologías Emergentes I",
                "Narrativa Audiovisual III",
                "Taller de Diseño Gráfico VII",
                "Laboratorio de Tecnologías Emergentes II",
                "Taller de Diseño Gráfico VIII",
                "Legislación y Práctica Profesional"
        );

        materias.forEach(nombre -> {
            if (!materiaRepository.existsByNombre(nombre)) {
                Materia materia = new Materia();
                materia.setNombre(nombre);
                materiaRepository.save(materia);
            }
        });
    }
}
