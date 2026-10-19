package com.elwey.restaurante.repository;

import com.elwey.restaurante.entities.Comida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComidaRepository extends JpaRepository<Comida, Long> {
    List<Comida> findByNombre(String nombre);
    List<Comida> findByNombreContainingIgnoreCase(String nombre);
    List<Comida> findByNombreContainingIgnoreCaseOrCategoriaNombreContainingIgnoreCase(String nombre, String categoriaNombre);
    List<Comida> findByActivoTrue();
}
