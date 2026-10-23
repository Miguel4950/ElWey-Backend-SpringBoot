package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Adicional;
import java.util.List;

/**
 * Interfaz de la capa de servicio para gestión de Adicionales.
 * Define operaciones CRUD y consultas especializadas por comida o categoría.
 */
public interface AdicionalService {

    List<Adicional> findAll();

    default List<Adicional> searchAll() {
        return findAll();
    }

    List<Adicional> listarActivos();

    Adicional findById(Long id);

    default Adicional searchById(Long id) {
        return findById(id);
    }

    List<Adicional> findByComidaId(Long comidaId);

    List<Adicional> findByCategoriaId(Long categoriaId);

    default List<Adicional> findByCategoria(Long categoriaId) {
        return findByCategoriaId(categoriaId);
    }

    Adicional save(Adicional adicional);

    void save(Adicional adicional, Long categoriaId);

    Adicional update(Long id, Adicional adicional);

    void delete(Long id);
}
