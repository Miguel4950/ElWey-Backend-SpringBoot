package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.OperadorDto;
import com.elwey.restaurante.entities.Operador;
import java.util.List;

/**
 * Interfaz de la capa de Servicio de Negocio para Operadores y su Autenticación.
 */
public interface OperadorService {

    List<Operador> listarTodos();

    Operador obtenerPorId(Long id);

    Operador obtenerPorUsuario(String usuario);

    OperadorDto autenticar(String usuario, String contrasena);

    Operador guardar(Operador operador);

    Operador actualizar(Long id, Operador operador);

    void eliminar(Long id);
}
