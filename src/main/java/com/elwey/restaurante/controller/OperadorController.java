package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.LoginRequestDto;
import com.elwey.restaurante.dto.OperadorDto;
import com.elwey.restaurante.entities.Operador;
import com.elwey.restaurante.service.OperadorService;
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
 * Controlador REST para Operadores y su Autenticación en el sistema "El Wey".
 */
@Slf4j
@Tag(name = "Operadores", description = "Endpoints para registro, edición, listado y login de operadores")
@RestController
@RequestMapping({ "/operador", "/operadores" })
@CrossOrigin(origins = "http://localhost:4200")
public class OperadorController {

    @Autowired
    private OperadorService operadorService;

    @Operation(summary = "Login para operadores (por usuario)")
    @PostMapping("/login")
    public ResponseEntity<OperadorDto> login(@RequestBody LoginRequestDto loginRequest) {
        log.info("Petición REST: autenticar operador {}", loginRequest != null ? loginRequest.getCorreo() : "null");
        String usuario = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;

        OperadorDto respuesta = operadorService.autenticar(usuario, contrasena);
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Listar todos los operadores registrados sin exponer sus contraseñas")
    @GetMapping({ "", "/", "/all", "/operadores" })
    public ResponseEntity<List<OperadorDto>> listarTodos() {
        log.info("Petición REST: listar todos los operadores");
        List<OperadorDto> dtos = operadorService.listarTodos().stream()
                .map(OperadorDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener un operador por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<OperadorDto> obtenerPorId(@PathVariable("id") Long id) {
        log.info("Petición REST: obtener operador con ID: {}", id);
        Operador operador = operadorService.obtenerPorId(id);
        return ResponseEntity.ok(OperadorDto.fromEntity(operador));
    }

    @Operation(summary = "Registrar un nuevo operador en la plataforma")
    @PostMapping({ "", "/", "/add" })
    public ResponseEntity<OperadorDto> registrarOperador(@RequestBody OperadorDto dto) {
        log.info("Petición REST: registrar operador: {}", dto.getUsuario());
        Operador operador = dto.toEntity();
        Operador guardado = operadorService.guardar(operador);
        return ResponseEntity.status(HttpStatus.CREATED).body(OperadorDto.fromEntity(guardado));
    }

    @Operation(summary = "Actualizar información de un operador")
    @PutMapping({ "/update", "/update/{id}", "/{id}" })
    public ResponseEntity<OperadorDto> actualizarOperador(
            @PathVariable(name = "id", required = false) Long id,
            @RequestBody OperadorDto dto) {
        Long targetId = (id != null) ? id : (dto != null ? dto.getId() : null);
        log.info("Petición REST: actualizando operador con ID: {}", targetId);
        Operador operador = (dto != null) ? dto.toEntity() : null;
        Operador actualizado = operadorService.actualizar(targetId, operador);
        return ResponseEntity.ok(OperadorDto.fromEntity(actualizado));
    }

    @Operation(summary = "Eliminar permanentemente un operador")
    @DeleteMapping({ "/delete/{id}", "/{id}" })
    public ResponseEntity<Void> eliminarOperador(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminando operador con ID: {}", id);
        operadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
