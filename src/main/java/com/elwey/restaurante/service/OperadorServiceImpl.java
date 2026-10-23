package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Operador;
import com.elwey.restaurante.errors.CredencialesInvalidasException;
import com.elwey.restaurante.errors.OperadorNotFoundException;
import com.elwey.restaurante.repository.OperadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la capa de servicio para Operador.
 * Encapsula la persistencia, validaciones y reglas de negocio sin exponer excepciones checked.
 */
@Service
public class OperadorServiceImpl implements OperadorService {

    @Autowired
    private OperadorRepository operadorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Operador> listarTodos() {
        return operadorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Operador obtenerPorId(Long id) {
        return operadorRepository.findById(id)
                .orElseThrow(() -> new OperadorNotFoundException(id));
    }

    @Override
    @Transactional
    public Operador guardar(Operador operador) {
        if (operador.getUsuario() != null) {
            operador.setUsuario(operador.getUsuario().trim().toLowerCase());
        }
        return operadorRepository.save(operador);
    }

    @Override
    @Transactional
    public Operador actualizar(Long id, Operador datosActualizados) {
        Operador existente = obtenerPorId(id);
        if (datosActualizados.getNombre() != null && !datosActualizados.getNombre().isBlank()) {
            existente.setNombre(datosActualizados.getNombre().trim());
        }
        if (datosActualizados.getUsuario() != null && !datosActualizados.getUsuario().isBlank()) {
            existente.setUsuario(datosActualizados.getUsuario().trim().toLowerCase());
        }
        if (datosActualizados.getContrasena() != null && !datosActualizados.getContrasena().isBlank()) {
            existente.setContrasena(datosActualizados.getContrasena().trim());
        }
        return operadorRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Operador existente = obtenerPorId(id);
        operadorRepository.delete(existente);
    }

    @Override
    @Transactional(readOnly = true)
    public Operador obtenerPorUsuario(String usuario) {
        if (usuario == null) return null;
        return operadorRepository.findByUsuario(usuario.trim().toLowerCase()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Operador autenticar(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) {
            throw new IllegalArgumentException("Debe proporcionar usuario y contraseña.");
        }
        Operador op = operadorRepository.findByUsuario(usuario.trim().toLowerCase())
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario u operador no encontrado."));

        if (!contrasena.equals(op.getContrasena())) {
            throw new CredencialesInvalidasException("Contraseña incorrecta para el operador.");
        }
        return op;
    }
}
