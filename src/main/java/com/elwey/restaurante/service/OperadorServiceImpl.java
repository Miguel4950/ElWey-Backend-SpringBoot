package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.OperadorDto;
import com.elwey.restaurante.entities.Operador;
import com.elwey.restaurante.errors.CredencialesInvalidasException;
import com.elwey.restaurante.errors.OperadorNotFoundException;
import com.elwey.restaurante.repository.OperadorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de negocio para Operadores y su Autenticación.
 */
@Slf4j
@Service
public class OperadorServiceImpl implements OperadorService {

    @Autowired
    private OperadorRepository operadorRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Operador> listarTodos() {
        log.info("Listando todos los operadores registrados");
        return operadorRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Operador obtenerPorId(Long id) {
        log.info("Buscando operador con ID: {}", id);
        return operadorRepo.findById(id)
                .orElseThrow(() -> new OperadorNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Operador obtenerPorUsuario(String usuario) {
        log.info("Buscando operador con usuario: {}", usuario);
        if (usuario == null) {
            return null;
        }
        return operadorRepo.findByUsuario(usuario.trim().toLowerCase()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public OperadorDto autenticar(String usuario, String contrasena) {
        log.info("Autenticando operador por usuario: {}", usuario);

        if (usuario == null || usuario.trim().isEmpty() || contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar usuario y contraseña.");
        }

        String limpio = usuario.trim().toLowerCase();

        Operador operador = operadorRepo.findByUsuario(limpio).orElse(null);
        if (operador != null && contrasena.equals(operador.getContrasena())) {
            return OperadorDto.fromEntity(operador);
        } else {
            throw new CredencialesInvalidasException("Credenciales inválidas. Verifica tu usuario y contraseña.");
        }
    }

    @Override
    @Transactional
    public Operador guardar(Operador operador) {
        log.info("Guardando operador: {}", (operador != null ? operador.getNombre() : "null"));
        if (operador == null || operador.getUsuario() == null || operador.getUsuario().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no es válido.");
        }
        String usuarioLimpio = operador.getUsuario().trim().toLowerCase();
        Operador existente = obtenerPorUsuario(usuarioLimpio);
        if (existente != null) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }
        operador.setUsuario(usuarioLimpio);
        return operadorRepo.save(operador);
    }

    @Override
    @Transactional
    public Operador actualizar(Long id, Operador datosModificados) {
        log.info("Actualizando operador con ID: {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Debe proporcionar un ID válido para actualizar el operador.");
        }

        Operador actual = obtenerPorId(id);

        if (datosModificados.getNombre() != null && !datosModificados.getNombre().isBlank()) {
            actual.setNombre(datosModificados.getNombre().trim());
        }

        // Validar y actualizar usuario si cambió
        if (datosModificados.getUsuario() != null && !datosModificados.getUsuario().isBlank()) {
            String nuevoUsuario = datosModificados.getUsuario().trim().toLowerCase();
            if (!nuevoUsuario.equalsIgnoreCase(actual.getUsuario())) {
                Operador existente = obtenerPorUsuario(nuevoUsuario);
                if (existente != null && !existente.getId().equals(id)) {
                    throw new IllegalArgumentException("El usuario ya existe o no es válido.");
                }
                actual.setUsuario(nuevoUsuario);
            }
        }

        return operadorRepo.save(actual);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando operador con ID: {}", id);
        Operador operador = operadorRepo.findById(id)
                .orElseThrow(() -> new OperadorNotFoundException(id));
        operadorRepo.delete(operador);
    }
}
