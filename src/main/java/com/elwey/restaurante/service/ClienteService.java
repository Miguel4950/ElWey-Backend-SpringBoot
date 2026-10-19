package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Cliente;
import java.util.List;

/**
 * Interfaz de la capa de Servicio de Negocio para Clientes.
 */
public interface ClienteService {

    List<Cliente> listarTodos();

    Cliente obtenerPorId(Long id);

    default Cliente obtenerPorId(Integer id) {
        return id != null ? obtenerPorId(id.longValue()) : null;
    }

    Cliente obtenerPorEmail(String email);

    Cliente autenticar(String email, String password);

    Cliente guardar(Cliente cliente);

    Cliente actualizar(Long id, Cliente cliente);

    void eliminar(Long id);

    default void eliminar(Integer id) {
        if (id != null) {
            eliminar(id.longValue());
        }
    }

    default List<Cliente> searchAll() {
        return listarTodos();
    }

    default Cliente searchById(Long id) {
        return obtenerPorId(id);
    }
}