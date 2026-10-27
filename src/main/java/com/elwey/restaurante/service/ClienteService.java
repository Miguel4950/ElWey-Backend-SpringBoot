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

    Cliente obtenerPorEmail(String email);

    ClienteDto autenticar(String email, String password);

    Cliente guardar(Cliente cliente);

    Cliente actualizar(Long id, Cliente cliente);

    Cliente desactivarCliente(Long id);

    void eliminar(Long id);

}
