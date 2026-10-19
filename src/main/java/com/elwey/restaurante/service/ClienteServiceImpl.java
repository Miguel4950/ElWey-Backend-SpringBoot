package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Operador;
import com.elwey.restaurante.errors.ClienteConPedidosException;
import com.elwey.restaurante.errors.ClienteNotFoundException;
import com.elwey.restaurante.errors.CredencialesInvalidasException;
import com.elwey.restaurante.repository.ClienteRepository;
import com.elwey.restaurante.repository.OperadorRepository;
import com.elwey.restaurante.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository repo;

    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private OperadorRepository operadorRepo;

    Logger log = LoggerFactory.getLogger(ClienteServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        log.info("Listando todos los clientes desde el servicio");
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
        log.info("Buscando cliente con ID: {}", id);
        return repo.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorEmail(String email) {
        log.info("Buscando cliente con email: " + email);
        if (email == null) {
            return null;
        }
        return repo.findByEmail(email.trim()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente autenticar(String email, String password) {
        log.info("Autenticando cliente con email: {}", email);

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar correo y contraseña.");
        }

        Cliente cliente = obtenerPorEmail(email.trim());
        if (cliente == null) {
            Operador op = operadorRepo.findByUsuario(email.trim()).orElse(null);
            if (op != null && password.equals(op.getContrasena())) {
                Cliente opCliente = obtenerPorEmail("operador@elwey.com");
                if (opCliente != null) {
                    return opCliente;
                }
                return Cliente.builder()
                        .id(op.getId())
                        .nombre(op.getNombre())
                        .email("operador@elwey.com")
                        .telefono("+57 300 000 0002")
                        .direccion("Sede Cocina y Despachos, Bogotá")
                        .activo(true)
                        .build();
            }
        }

        if (cliente == null || !password.equals(cliente.getPassword())) {
            throw new CredencialesInvalidasException("Credenciales inválidas. Verifica tu correo y contraseña.");
        }

        if (!cliente.getActivo()) {
            throw new IllegalStateException("La cuenta de usuario se encuentra inactiva.");
        }

        return cliente;
    }

    @Override
    @Transactional
    public Cliente guardar(Cliente cliente) {
        log.info("Guardando o actualizando cliente: " + (cliente != null ? cliente.getNombre() : "null"));
        if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo no es válido.");
        }
        Cliente existente = obtenerPorEmail(cliente.getEmail());
        if (existente != null) {
            throw new IllegalArgumentException("El correo ya esta en uso.");
        }
        if (cliente.getFechaRegistro() == null || cliente.getFechaRegistro().isBlank()) {
            cliente.setFechaRegistro(java.time.LocalDate.now().toString());
        }
        return repo.save(cliente);
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
        /*if (datosModificados.getApellido() != null) {
            actual.setApellido(datosModificados.getApellido().trim());
        }*/
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

        return repo.save(actual);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando cliente con ID: {}", id);
        Cliente cliente = repo.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));

        List<com.elwey.restaurante.entities.Pedido> pedidos = pedidoRepo.findByClienteId(id);
        if (pedidos != null && !pedidos.isEmpty()) {
            throw new ClienteConPedidosException(cliente.getNombre());
        }

        repo.deleteById(id);
    }
}