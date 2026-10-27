package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Comida;
import java.util.List;

/**
 * Interfaz de la capa de servicio para la gestión de Comidas.
 */
public interface ComidaService {

    List<Comida> listarTodos();

    List<Comida> listarActivas();

    Comida obtenerPorId(Long id);

    void save(Comida comida, Long categoriaId);

    void eliminar(Long id);

    List<Comida> buscarPorNombre(String query);
}
