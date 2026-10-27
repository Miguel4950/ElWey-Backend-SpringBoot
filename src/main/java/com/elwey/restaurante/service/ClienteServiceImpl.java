package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.ClienteDto;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.errors.ClienteConPedidosException;
import com.elwey.restaurante.errors.ClienteNotFoundException;
import com.elwey.restaurante.errors.CredencialesInvalidasException;
import com.elwey.restaurante.repository.ClienteRepository;
import com.elwey.restaurante.repository.PedidoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación del servicio de negocio para Clientes y Autenticación
 * Multi-Rol.
 * Valida credenciales contra las tablas Cliente, Operador y Administrador.
 */
@Slf4j
@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private PedidoRepository pedidoRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        log.info("Listando todos los clientes registrados");
        return clienteRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
        log.info("Buscando cliente con ID: {}", id);
        return clienteRepo.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorEmail(String email) {
        log.info("Buscando cliente con email: {}", email);
        if (email == null) {
            return null;
        }
        return clienteRepo.findByEmail(email.trim()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDto autenticar(String correo, String password) {
        log.info("Autenticando cliente por correo: {}", correo);

        if (correo == null || correo.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar correo y contraseña.");
        }

        String limpio = correo.trim().toLowerCase();

        Cliente cliente = clienteRepo.findByEmail(limpio).orElse(null);
        if (cliente != null && password.equals(cliente.getPassword())) {

            if (cliente.getActivo()) {
                return ClienteDto.fromEntity(cliente);

            } else {
                throw new IllegalStateException("La cuenta de usuario se encuentra inactiva.");
            }

        } else {
            throw new CredencialesInvalidasException("Credenciales inválidas. Verifica tu correo y contraseña.");
        }
    }

    @Override
    @Transactional
    public Cliente guardar(Cliente cliente) {
        log.info("Guardando cliente: {}", (cliente != null ? cliente.getNombre() : "null"));
        if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo no es válido.");
        }
        Cliente existente = obtenerPorEmail(cliente.getEmail());
        if (existente != null) {
            throw new IllegalArgumentException("El correo ya está en uso.");
        }
        cliente.setFechaRegistro(LocalDate.now().toString());
        return clienteRepo.save(cliente);
    }

    @Override
    @Transactional
    public Cliente actualizar(Long id, Cliente datosModificados) {
        log.info("Actualizando cliente con ID: {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Debe proporcionar un ID válido para actualizar el cliente.");
        }

        Cliente actual = obtenerPorId(id);

        if (datosModificados.getNombre() != null && !datosModificados.getNombre().isBlank()) {
            actual.setNombre(datosModificados.getNombre().trim());
        }
        if (datosModificados.getApellido() != null && !datosModificados.getApellido().isBlank()) {
            actual.setApellido(datosModificados.getApellido().trim());
        }
        if (datosModificados.getTelefono() != null) {
            actual.setTelefono(datosModificados.getTelefono().trim());
        }
        if (datosModificados.getDireccion() != null) {
            actual.setDireccion(datosModificados.getDireccion().trim());
        }

        // Validar y actualizar correo si cambió
        if (datosModificados.getEmail() != null && !datosModificados.getEmail().isBlank()) {
            String nuevoEmail = datosModificados.getEmail().trim();
            if (!nuevoEmail.equalsIgnoreCase(actual.getEmail())) {
                Cliente existente = obtenerPorEmail(nuevoEmail);
                if (existente != null && !existente.getId().equals(id)) {
                    throw new IllegalArgumentException("El correo ya existe o no es válido.");
                }
                actual.setEmail(nuevoEmail);
            }
        }

        return clienteRepo.save(actual);
    }

    @Override
    @Transactional
    public Cliente desactivarCliente(Long id) {
        log.info("Desactivando cliente con ID: {}", id);
        Cliente cliente = clienteRepo.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        cliente.setActivo(false);
        return clienteRepo.save(cliente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando cliente con ID: {}", id);
        Cliente cliente = clienteRepo.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));

        List<Pedido> pedidos = pedidoRepo.findByClienteId(id);
        if (pedidos != null && !pedidos.isEmpty()) {
            throw new ClienteConPedidosException(cliente.getNombre());
        }

        clienteRepo.deleteById(id);
    }
}
