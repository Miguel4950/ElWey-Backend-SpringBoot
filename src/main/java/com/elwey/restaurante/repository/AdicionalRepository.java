package com.elwey.restaurante.repository;

import com.elwey.restaurante.entities.Adicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad Adicional.
 */
@Repository
public interface AdicionalRepository extends JpaRepository<Adicional, Long> {
    List<Adicional> findByCategoriasId(Long categoriaId);
    List<Adicional> findByComidasId(Long comidaId);
    List<Adicional> findByActivoTrue();
    List<Adicional> findByNombre(String nombre);
}
