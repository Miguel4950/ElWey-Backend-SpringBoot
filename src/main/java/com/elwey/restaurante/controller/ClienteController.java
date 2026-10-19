package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.ClienteDto;
import com.elwey.restaurante.dto.LoginRequestDto;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.errors.CredencialesInvalidasException;
import com.elwey.restaurante.service.ClienteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping({ "/api", "/cliente" })
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * POST /api/auth/login
     * Recibe { correo, contrasena }. Retorna { id, nombre, correo, telefono, direccion, activo }
     * si es válido o 401 Unauthorized.
     */
    @PostMapping({ "/auth/login", "/login" })
    public ResponseEntity<ClienteDto> login(@RequestBody LoginRequestDto loginRequest) {
        log.info("Petición REST: autenticar usuario {}", loginRequest != null ? loginRequest.getCorreo() : "null");
        String correo = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;

        Cliente cliente = clienteService.autenticar(correo, contrasena);
        return ResponseEntity.ok(ClienteDto.fromEntity(cliente));
    }

    /**
     * GET /api/clientes, GET /cliente/all
     * Retorna el listado completo de clientes sin contraseñas.
     */
    @GetMapping({ "/clientes", "/all" })
    public ResponseEntity<List<ClienteDto>> listarTodos() {
        log.info("Petición REST: listar todos los clientes");
        List<ClienteDto> dtos = clienteService.listarTodos().stream()
                .map(ClienteDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/clientes/{id}
     * Retorna los datos de perfil del cliente sin incluir la contraseña. (Status: 200 OK).
     */
    @GetMapping({ "/clientes/{id}", "/{id}", "/find/{id}", "/portal/{id}" })
    public ResponseEntity<ClienteDto> obtenerPorId(@PathVariable("id") Long id) {
        log.info("Petición REST: obtener cliente con ID: {}", id);
        Cliente cliente = clienteService.obtenerPorId(id);
        return ResponseEntity.ok(ClienteDto.fromEntity(cliente));
    }

    /**
     * POST /api/clientes
     * Recibe datos de registro { nombre, correo, contrasena, telefono, direccion } y retorna el cliente creado con su ID. (Status: 201 Created).
     */
    @PostMapping({ "/clientes", "/add" })
    public ResponseEntity<ClienteDto> registrarCliente(@RequestBody ClienteDto dto) {
        log.info("Petición REST: registrar cliente: {}", dto.getCorreo());
        Cliente cliente = dto.toEntity();
        Cliente guardado = clienteService.guardar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteDto.fromEntity(guardado));
    }

    /**
     * PUT /api/clientes/{id}
     * Recibe los datos modificados del cliente { nombre, correo, telefono, direccion } y retorna el perfil actualizado. (Status: 200 OK).
     */
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

    /**
     * DELETE /api/clientes/{id}
     * Elimina la cuenta del cliente. (Status: 204 No Content).
     */
    @DeleteMapping({ "/clientes/{id}", "/delete/{id}", "/eliminar/{id}", "/{id}" })
    public ResponseEntity<Void> eliminarCliente(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminando cuenta del cliente ID: {}", id);
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
