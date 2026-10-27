package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.errors.CredencialesInvalidasException;
import com.elwey.restaurante.errors.DomiciliarioNotFoundException;
import com.elwey.restaurante.repository.DomiciliarioRepository;
import com.elwey.restaurante.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la capa de servicio para Domiciliario.
 * Gestiona la disponibilidad y el ciclo de vida del personal de reparto.
 */
@Service
public class DomiciliarioServiceImpl implements DomiciliarioService {

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Domiciliario> searchAll() {
        return domiciliarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Domiciliario> listarDisponibles() {
        return domiciliarioRepository.findByDisponible(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Domiciliario searchById(Long id) {
        return domiciliarioRepository.findById(id)
                .orElseThrow(() -> new DomiciliarioNotFoundException(id));
    }

    @Override
    @Transactional
    public Domiciliario save(Domiciliario domiciliario) {
        if (domiciliario.getDisponible() == null) {
            domiciliario.setDisponible(true);
        }
        return domiciliarioRepository.save(domiciliario);
    }

    @Override
    @Transactional
    public Domiciliario actualizar(Long id, Domiciliario datos) {
        Domiciliario existente = searchById(id);
        if (datos.getCedula() != null && !datos.getCedula().isBlank()) {
            existente.setCedula(datos.getCedula().trim());
        }
        if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
            existente.setNombre(datos.getNombre().trim());
        }
        if (datos.getCelular() != null && !datos.getCelular().isBlank()) {
            existente.setCelular(datos.getCelular().trim());
        }
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            existente.setContrasena(datos.getContrasena().trim());
        }
        if (datos.getDisponible() != null) {
            existente.setDisponible(datos.getDisponible());
        }
        return domiciliarioRepository.save(existente);
    }

    @Override
    @Transactional
    public Domiciliario actualizarDisponibilidad(Long id, Boolean disponible) {
        Domiciliario existente = searchById(id);
        if (disponible != null) {
            existente.setDisponible(disponible);
        } else {
            // Si viene nulo, invierte el estado actual
            existente.setDisponible(!Boolean.TRUE.equals(existente.getDisponible()));
        }
        return domiciliarioRepository.save(existente);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Domiciliario domiciliario = searchById(id);
        // Desvincular de pedidos asociados para preservar integridad referencial sin errores de constraint
        List<Pedido> pedidosAsociados = pedidoRepository.findByDomiciliarioId(id);
        if (pedidosAsociados != null && !pedidosAsociados.isEmpty()) {
            for (Pedido p : pedidosAsociados) {
                p.setDomiciliario(null);
                pedidoRepository.save(p);
            }
        }
        domiciliarioRepository.delete(domiciliario);
    }

    @Override
    @Transactional(readOnly = true)
    public Domiciliario autenticar(String identificador, String contrasena) {
        if (identificador == null || identificador.trim().isEmpty() || contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar cédula o celular y contraseña.");
        }

        String limpio = identificador.trim();

        // Buscar por cédula o por celular
        Domiciliario domiciliario = domiciliarioRepository.findByCedula(limpio)
                .or(() -> domiciliarioRepository.findByCelular(limpio))
                .orElseThrow(() -> new CredencialesInvalidasException("Domiciliario no encontrado con la cédula o celular proporcionado."));

        if (domiciliario.getContrasena() == null || !contrasena.equals(domiciliario.getContrasena())) {
            throw new CredencialesInvalidasException("Contraseña incorrecta para el domiciliario.");
        }

        return domiciliario;
    }
}
