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
 * Controlador REST para Clientes y su Autenticación en el sistema "El Wey".
 */
@Slf4j
@Tag(name = "Clientes", description = "Endpoints para registro, perfil, listado y login de clientes")
@RestController
@RequestMapping({ "/cliente", "/clientes" })
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "Login para clientes (por correo)")
    @PostMapping("/login")
    public ResponseEntity<ClienteDto> login(@RequestBody LoginRequestDto loginRequest) {
        log.info("Petición REST: autenticar cliente {}", loginRequest != null ? loginRequest.getCorreo() : "null");
        String correo = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;

        ClienteDto respuesta = clienteService.autenticar(correo, contrasena);
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Listar todos los clientes registrados sin exponer sus contraseñas")
    @GetMapping({ "", "/", "/clientes", "/all" })
    public ResponseEntity<List<ClienteDto>> listarTodos() {
        log.info("Petición REST: listar todos los clientes");
        List<ClienteDto> dtos = clienteService.listarTodos().stream()
                .map(ClienteDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener el perfil de un cliente por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> obtenerPorId(@PathVariable("id") Long id) {
        log.info("Petición REST: obtener cliente con ID: {}", id);
        Cliente cliente = clienteService.obtenerPorId(id);
        return ResponseEntity.ok(ClienteDto.fromEntity(cliente));
    }

    @Operation(summary = "Registrar un nuevo cliente en la plataforma")
    @PostMapping({ "", "/", "/add" })
    public ResponseEntity<ClienteDto> registrarCliente(@RequestBody ClienteDto dto) {
        log.info("Petición REST: registrar cliente: {}", dto.getCorreo());
        Cliente cliente = dto.toEntity();
        Cliente guardado = clienteService.guardar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteDto.fromEntity(guardado));
    }

    @Operation(summary = "Actualizar información de perfil de un cliente")
    @PutMapping({ "/update", "/update/{id}", "/{id}" })
    public ResponseEntity<ClienteDto> actualizarCliente(
            @PathVariable(name = "id", required = false) Long id,
            @RequestBody ClienteDto dto) {
        Long targetId = (id != null) ? id : (dto != null ? dto.getId() : null);
        log.info("Petición REST: actualizando cliente con ID: {}", targetId);
        Cliente cliente = (dto != null) ? dto.toEntity() : null;
        Cliente actualizado = clienteService.actualizar(targetId, cliente);
        return ResponseEntity.ok(ClienteDto.fromEntity(actualizado));
    }

    @Operation(summary = "Desactivar la cuenta de un cliente sin borrar su historial")
    @PutMapping("/desactivar/{id}")
    public ResponseEntity<ClienteDto> desactivarCliente(@PathVariable("id") Long id) {
        log.info("Petición REST: desactivando cuenta del cliente ID: {}", id);
        Cliente desactivado = clienteService.desactivarCliente(id);
        return ResponseEntity.ok(ClienteDto.fromEntity(desactivado));
    }

    @Operation(summary = "Eliminar permanentemente la cuenta de un cliente")
    @DeleteMapping({ "/delete/{id}", "/{id}" })
    public ResponseEntity<Void> eliminarCliente(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminando cuenta del cliente ID: {}", id);
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
