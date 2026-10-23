package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Domiciliario;
import java.util.List;

/**
 * Interfaz de la capa de servicio para Domiciliarios.
 * Maneja operaciones CRUD y control de disponibilidad en tiempo real.
 */
public interface DomiciliarioService {

    List<Domiciliario> searchAll();

    default List<Domiciliario> listarTodos() {
        return searchAll();
    }

    List<Domiciliario> listarDisponibles();

    Domiciliario searchById(Long id);

    Domiciliario save(Domiciliario domiciliario);

    Domiciliario actualizar(Long id, Domiciliario domiciliario);

    Domiciliario actualizarDisponibilidad(Long id, Boolean disponible);

    void delete(Long id);
}
