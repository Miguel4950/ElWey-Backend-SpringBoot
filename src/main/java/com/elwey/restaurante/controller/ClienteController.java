package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.ClienteDto;
import com.elwey.restaurante.dto.LoginRequestDto;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para Clientes y Autenticación del sistema "El Wey".
 * Soporta login unificado multi-rol (Cliente, Operador, Administrador) y logins específicos por rol.
 */
@Slf4j
@Tag(name = "Clientes y Autenticación", description = "Endpoints para registro, perfil de cliente y login multi-rol")
@RestController
@RequestMapping({ "/api", "/cliente" })
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "Autenticación unificada para Cliente, Operador o Administrador (devuelve DTO con rol)")
    @PostMapping({ "/auth/login", "/login" })
    public ResponseEntity<ClienteDto> login(@RequestBody LoginRequestDto loginRequest) {
        log.info("Petición REST: autenticar usuario {}", loginRequest != null ? loginRequest.getCorreo() : "null");
        String correo = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;

        ClienteDto respuesta = clienteService.autenticarUsuario(correo, contrasena);
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Login específico para rol Cliente")
    @PostMapping("/auth/login/cliente")
    public ResponseEntity<ClienteDto> loginCliente(@RequestBody LoginRequestDto loginRequest) {
        String correo = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;
        return ResponseEntity.ok(clienteService.autenticarPorRol(correo, contrasena, "CLIENTE"));
    }

    @Operation(summary = "Login específico para rol Operador de cocina")
    @PostMapping("/auth/login/operador")
    public ResponseEntity<ClienteDto> loginOperador(@RequestBody LoginRequestDto loginRequest) {
        String correo = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;
        return ResponseEntity.ok(clienteService.autenticarPorRol(correo, contrasena, "OPERADOR"));
    }

    @Operation(summary = "Login específico para rol Administrador")
    @PostMapping("/auth/login/admin")
    public ResponseEntity<ClienteDto> loginAdmin(@RequestBody LoginRequestDto loginRequest) {
        String correo = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;
        return ResponseEntity.ok(clienteService.autenticarPorRol(correo, contrasena, "ADMIN"));
    }

    @Operation(summary = "Listar todos los clientes registrados sin exponer sus contraseñas")
    @GetMapping({ "/clientes", "/all" })
    public ResponseEntity<List<ClienteDto>> listarTodos() {
        log.info("Petición REST: listar todos los clientes");
        List<ClienteDto> dtos = clienteService.listarTodos().stream()
                .map(ClienteDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener el perfil de un cliente por su ID")
    @GetMapping({ "/clientes/{id}", "/{id}", "/find/{id}", "/portal/{id}" })
    public ResponseEntity<ClienteDto> obtenerPorId(@PathVariable("id") Long id) {
        log.info("Petición REST: obtener cliente con ID: {}", id);
        Cliente cliente = clienteService.obtenerPorId(id);
        return ResponseEntity.ok(ClienteDto.fromEntity(cliente));
    }

    @Operation(summary = "Registrar un nuevo cliente en la plataforma")
    @PostMapping({ "/clientes", "/add" })
    public ResponseEntity<ClienteDto> registrarCliente(@RequestBody ClienteDto dto) {
        log.info("Petición REST: registrar cliente: {}", dto.getCorreo());
        Cliente cliente = dto.toEntity();
        Cliente guardado = clienteService.guardar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteDto.fromEntity(guardado));
    }

    @Operation(summary = "Actualizar información de perfil de un cliente")
    @PutMapping({ "/clientes/{id}", "/update", "/portal/actualizar/{id}" })
    public ResponseEntity<ClienteDto> actualizarCliente(
            @PathVariable(name = "id", required = false) Long id,
            @RequestBody ClienteDto dto) {
        Long targetId = (id != null) ? id : (dto != null ? dto.getId() : null);
        log.info("Petición REST: actualizando cliente con ID: {}", targetId);
        Cliente cliente = (dto != null) ? dto.toEntity() : null;
        Cliente actualizado = clienteService.actualizar(targetId, cliente);
        return ResponseEntity.ok(ClienteDto.fromEntity(actualizado));
    }

    @Operation(summary = "Eliminar permanentemente la cuenta de un cliente")
    @DeleteMapping({ "/clientes/{id}", "/delete/{id}", "/eliminar/{id}", "/{id}" })
    public ResponseEntity<Void> eliminarCliente(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminando cuenta del cliente ID: {}", id);
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
