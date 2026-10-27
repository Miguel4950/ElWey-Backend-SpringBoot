package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.DomiciliarioDto;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.errors.CredencialesInvalidasException;
import com.elwey.restaurante.errors.DomiciliarioNotFoundException;
import com.elwey.restaurante.repository.DomiciliarioRepository;
import com.elwey.restaurante.repository.PedidoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de negocio para Domiciliarios y su Autenticación.
 */
@Slf4j
@Service
public class DomiciliarioServiceImpl implements DomiciliarioService {

    @Autowired
    private DomiciliarioRepository domiciliarioRepo;

    @Autowired
    private PedidoRepository pedidoRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Domiciliario> listarTodos() {
        log.info("Listando todos los domiciliarios registrados");
        return domiciliarioRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Domiciliario> listarDisponibles() {
        log.info("Listando domiciliarios disponibles");
        return domiciliarioRepo.findByDisponible(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Domiciliario obtenerPorId(Long id) {
        log.info("Buscando domiciliario con ID: {}", id);
        return domiciliarioRepo.findById(id)
                .orElseThrow(() -> new DomiciliarioNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Domiciliario obtenerPorCedula(String cedula) {
        log.info("Buscando domiciliario con cédula: {}", cedula);
        if (cedula == null) {
            return null;
        }
        return domiciliarioRepo.findByCedula(cedula.trim()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public DomiciliarioDto autenticar(String identificador, String contrasena) {
        log.info("Autenticando domiciliario con identificador: {}", identificador);

        if (identificador == null || identificador.trim().isEmpty() || contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar cédula o celular y contraseña.");
        }

        String limpio = identificador.trim();

        Domiciliario domiciliario = domiciliarioRepo.findByCedula(limpio)
                .or(() -> domiciliarioRepo.findByCelular(limpio))
                .orElse(null);

        if (domiciliario != null && contrasena.equals(domiciliario.getContrasena())) {
            return DomiciliarioDto.fromEntity(domiciliario);
        } else {
            throw new CredencialesInvalidasException("Credenciales inválidas. Verifica tu cédula o celular y contraseña.");
        }
    }

    @Override
    @Transactional
    public Domiciliario guardar(Domiciliario domiciliario) {
        log.info("Guardando domiciliario: {}", (domiciliario != null ? domiciliario.getNombre() : "null"));
        if (domiciliario == null || domiciliario.getCedula() == null || domiciliario.getCedula().isBlank()) {
            throw new IllegalArgumentException("La cédula no es válida.");
        }
        if (domiciliario.getContrasena() == null || domiciliario.getContrasena().isBlank()) {
            throw new IllegalArgumentException("Debe proporcionar una contraseña para el domiciliario.");
        }
        Domiciliario existente = obtenerPorCedula(domiciliario.getCedula());
        if (existente != null) {
            throw new IllegalArgumentException("La cédula ya está en uso.");
        }
        if (domiciliario.getDisponible() == null) {
            domiciliario.setDisponible(true);
        }
        return domiciliarioRepo.save(domiciliario);
    }

    @Override
    @Transactional
    public Domiciliario actualizar(Long id, Domiciliario datosModificados) {
        log.info("Actualizando domiciliario con ID: {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Debe proporcionar un ID válido para actualizar el domiciliario.");
        }

        Domiciliario actual = obtenerPorId(id);

        if (datosModificados.getNombre() != null && !datosModificados.getNombre().isBlank()) {
            actual.setNombre(datosModificados.getNombre().trim());
        }
        if (datosModificados.getCelular() != null && !datosModificados.getCelular().isBlank()) {
            actual.setCelular(datosModificados.getCelular().trim());
        }
        if (datosModificados.getDisponible() != null) {
            actual.setDisponible(datosModificados.getDisponible());
        }

        // Validar y actualizar cédula si cambió
        if (datosModificados.getCedula() != null && !datosModificados.getCedula().isBlank()) {
            String nuevaCedula = datosModificados.getCedula().trim();
            if (!nuevaCedula.equalsIgnoreCase(actual.getCedula())) {
                Domiciliario existente = obtenerPorCedula(nuevaCedula);
                if (existente != null && !existente.getId().equals(id)) {
                    throw new IllegalArgumentException("La cédula ya existe o no es válida.");
                }
                actual.setCedula(nuevaCedula);
            }
        }

        return domiciliarioRepo.save(actual);
    }

    @Override
    @Transactional
    public Domiciliario actualizarDisponibilidad(Long id, Boolean disponible) {
        log.info("Actualizando disponibilidad de domiciliario con ID: {} a {}", id, disponible);
        Domiciliario existente = obtenerPorId(id);
        if (disponible != null) {
            existente.setDisponible(disponible);
        } else {
            existente.setDisponible(!Boolean.TRUE.equals(existente.getDisponible()));
        }
        return domiciliarioRepo.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando domiciliario con ID: {}", id);
        Domiciliario domiciliario = obtenerPorId(id);

        List<Pedido> pedidosAsociados = pedidoRepo.findByDomiciliarioId(id);
        if (pedidosAsociados != null && !pedidosAsociados.isEmpty()) {
            for (Pedido p : pedidosAsociados) {
                p.setDomiciliario(null);
                pedidoRepo.save(p);
            }
        }
        domiciliarioRepo.delete(domiciliario);
    }
}
