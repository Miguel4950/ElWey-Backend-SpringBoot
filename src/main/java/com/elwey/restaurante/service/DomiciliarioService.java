package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.DomiciliarioDto;
import com.elwey.restaurante.entities.Domiciliario;
import java.util.List;

/**
 * Interfaz de la capa de Servicio de Negocio para Domiciliarios y su Autenticación.
 */
public interface DomiciliarioService {

    List<Domiciliario> listarTodos();

    List<Domiciliario> listarDisponibles();

    Domiciliario obtenerPorId(Long id);

    Domiciliario obtenerPorCedula(String cedula);

    DomiciliarioDto autenticar(String identificador, String contrasena);

    Domiciliario guardar(Domiciliario domiciliario);

    Domiciliario actualizar(Long id, Domiciliario domiciliario);

    Domiciliario actualizarDisponibilidad(Long id, Boolean disponible);

    void eliminar(Long id);
}
