package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Operador;
import java.util.List;

/**
 * Interfaz de la capa de servicio para Operadores de cocina y despachos.
 * Define operaciones CRUD completas y autenticación.
 */
public interface OperadorService {

    List<Operador> listarTodos();

    Operador obtenerPorId(Long id);

    Operador guardar(Operador operador);

    Operador actualizar(Long id, Operador operador);

    void eliminar(Long id);

    Operador obtenerPorUsuario(String usuario);

    Operador autenticar(String usuario, String contrasena);
}
