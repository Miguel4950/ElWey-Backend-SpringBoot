package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Adicional;
import java.util.List;

/**
 * Interfaz de la capa de servicio para gestión de Adicionales.
 * Define operaciones CRUD y consultas especializadas por comida o categoría.
 */
public interface AdicionalService {

    List<Adicional> findAll();

    List<Adicional> listarActivos();

    Adicional findById(Long id);

    List<Adicional> findByComidaId(Long comidaId);

    List<Adicional> findByCategoriaId(Long categoriaId);

    Adicional save(Adicional adicional);

    Adicional update(Long id, Adicional adicional);

    void delete(Long id);
}
