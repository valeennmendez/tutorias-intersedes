package com.example.tutorias.specification;

import com.example.tutorias.entity.EstadoTutoria;
import com.example.tutorias.entity.ModalidadTutoria;
import com.example.tutorias.entity.Sede;
import com.example.tutorias.entity.Tutoria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TutoriaSpecification {

    public static Specification<Tutoria> conFiltros(String materiaNombre, Sede sede, ModalidadTutoria modalidad) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            predicados.add(cb.equal(root.get("estado"), EstadoTutoria.ACTIVA));

            // 1. Filtro por Materia (LIKE ignorando mayúsculas)
            if (materiaNombre != null && !materiaNombre.trim().isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("materia").get("nombre")), "%" + materiaNombre.toLowerCase() + "%"));
            }

            // 2. Filtro por Sede (Exacto por Enum)
            if (sede != null) {
                predicados.add(cb.equal(root.get("sede"), sede));
            }

            // 3. Filtro por Modalidad (Exacto por Enum)
            if (modalidad != null) {
                predicados.add(cb.equal(root.get("modalidad"), modalidad));
            }

            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }
}
