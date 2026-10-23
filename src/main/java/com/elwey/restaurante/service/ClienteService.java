package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.ClienteDto;
import com.elwey.restaurante.entities.Cliente;
import java.util.List;

/**
 * Interfaz de la capa de Servicio de Negocio para Clientes y Autenticación.
 */
public interface ClienteService {

    List<Cliente> listarTodos();

    Cliente obtenerPorId(Long id);

    default Cliente obtenerPorId(Integer id) {
        return id != null ? obtenerPorId(id.longValue()) : null;
    }

    Cliente obtenerPorEmail(String email);

    Cliente autenticar(String email, String password);

    ClienteDto autenticarUsuario(String login, String password);

    ClienteDto autenticarPorRol(String login, String password, String rolEsperado);

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
