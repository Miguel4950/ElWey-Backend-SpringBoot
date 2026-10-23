package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.ClienteDto;
import com.elwey.restaurante.entities.Administrador;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Operador;
import com.elwey.restaurante.errors.ClienteConPedidosException;
import com.elwey.restaurante.errors.ClienteNotFoundException;
import com.elwey.restaurante.errors.CredencialesInvalidasException;
import com.elwey.restaurante.repository.AdministradorRepository;
import com.elwey.restaurante.repository.ClienteRepository;
import com.elwey.restaurante.repository.OperadorRepository;
import com.elwey.restaurante.repository.PedidoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de negocio para Clientes y Autenticación Multi-Rol.
 * Valida credenciales contra las tablas Cliente, Operador y Administrador.
 */
@Slf4j
@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository repo;

    @Autowired
    private OperadorRepository operadorRepo;

    @Autowired
    private AdministradorRepository administradorRepo;

    @Autowired
    private PedidoRepository pedidoRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        log.info("Listando todos los clientes registrados");
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
        log.info("Buscando cliente con email: {}", email);
        if (email == null) {
            return null;
        }
        return repo.findByEmail(email.trim()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDto autenticarUsuario(String login, String password) {
        log.info("Autenticando usuario (3 roles) para login: {}", login);

        if (login == null || login.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar usuario/correo y contraseña.");
        }

        String limpio = login.trim().toLowerCase();

        // 1. Verificar en tabla Administrador (por usuario o email)
        Administrador adm = administradorRepo.findByUsuario(limpio).orElse(null);
        if (adm != null && password.equals(adm.getContrasena())) {
            return ClienteDto.fromAdministrador(adm);
        }

        // 2. Verificar en tabla Operador (por usuario o email)
        Operador op = operadorRepo.findByUsuario(limpio).orElse(null);
        if (op != null && password.equals(op.getContrasena())) {
            return ClienteDto.fromOperador(op);
        }

        // 3. Verificar en tabla Cliente (por email)
        Cliente cliente = repo.findByEmail(limpio).orElse(null);
        if (cliente != null && password.equals(cliente.getPassword())) {
            if (!Boolean.TRUE.equals(cliente.getActivo())) {
                throw new IllegalStateException("La cuenta de usuario se encuentra inactiva.");
            }
            return ClienteDto.fromCliente(cliente);
        }

        // 4. Intentar resolver por usuario en Cliente si coincide nombre o correo parcial
        if (cliente == null) {
            List<Cliente> todos = repo.findAll();
            for (Cliente c : todos) {
                if (c.getEmail() != null && c.getEmail().toLowerCase().startsWith(limpio + "@") && password.equals(c.getPassword())) {
                    if (!Boolean.TRUE.equals(c.getActivo())) {
                        throw new IllegalStateException("La cuenta de usuario se encuentra inactiva.");
                    }
                    return ClienteDto.fromCliente(c);
                }
            }
        }

        throw new CredencialesInvalidasException("Credenciales inválidas. Verifica tu correo o usuario y contraseña.");
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDto autenticarPorRol(String login, String password, String rolEsperado) {
        ClienteDto dto = autenticarUsuario(login, password);
        if (rolEsperado != null && !rolEsperado.isBlank()) {
            if (!rolEsperado.equalsIgnoreCase(dto.getRol())) {
                throw new CredencialesInvalidasException("Las credenciales ingresadas no corresponden a un usuario con rol " + rolEsperado.toUpperCase());
            }
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente autenticar(String email, String password) {
        ClienteDto dto = autenticarUsuario(email, password);
        Cliente cliente = obtenerPorEmail(dto.getCorreo());
        if (cliente != null) {
            return cliente;
        }
        return Cliente.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .email(dto.getCorreo())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .activo(dto.getActivo())
                .build();
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
