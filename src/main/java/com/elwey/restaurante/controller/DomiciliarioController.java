package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.DomiciliarioDto;
import com.elwey.restaurante.dto.LoginRequestDto;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.service.DomiciliarioService;
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
 * Controlador REST para Domiciliarios y su Autenticación en el sistema "El Wey".
 */
@Slf4j
@Tag(name = "Domiciliarios", description = "Endpoints para registro, disponibilidad, listado y login de domiciliarios")
@RestController
@RequestMapping({ "/domiciliario", "/api/domiciliarios" })
@CrossOrigin(origins = "http://localhost:4200")
public class DomiciliarioController {

    @Autowired
    private DomiciliarioService domiciliarioService;

    @Operation(summary = "Login para domiciliarios (por cédula o celular)")
    @PostMapping({ "/login", "/auth/login" })
    public ResponseEntity<DomiciliarioDto> login(@RequestBody LoginRequestDto loginRequest) {
        log.info("Petición REST: autenticar domiciliario {}", loginRequest != null ? loginRequest.getCorreo() : "null");
        String identificador = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;

        DomiciliarioDto respuesta = domiciliarioService.autenticar(identificador, contrasena);
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Listar todos los domiciliarios registrados sin exponer sus contraseñas")
    @GetMapping({ "", "/", "/all", "/domiciliarios" })
    public ResponseEntity<List<DomiciliarioDto>> listarTodos() {
        log.info("Petición REST: listar todos los domiciliarios");
        List<DomiciliarioDto> dtos = domiciliarioService.listarTodos().stream()
                .map(DomiciliarioDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Listar únicamente los domiciliarios disponibles para asignación")
    @GetMapping("/disponibles")
    public ResponseEntity<List<DomiciliarioDto>> listarDisponibles() {
        log.info("Petición REST: listar domiciliarios disponibles");
        List<DomiciliarioDto> dtos = domiciliarioService.listarDisponibles().stream()
                .map(DomiciliarioDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener un domiciliario por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<DomiciliarioDto> obtenerPorId(@PathVariable("id") Long id) {
        log.info("Petición REST: obtener domiciliario con ID: {}", id);
        Domiciliario domiciliario = domiciliarioService.obtenerPorId(id);
        return ResponseEntity.ok(DomiciliarioDto.fromEntity(domiciliario));
    }

    @Operation(summary = "Registrar un nuevo domiciliario en la plataforma")
    @PostMapping({ "", "/", "/add" })
    public ResponseEntity<DomiciliarioDto> registrarDomiciliario(@RequestBody DomiciliarioDto dto) {
        log.info("Petición REST: registrar domiciliario: {}", dto.getNombre());
        Domiciliario domiciliario = dto.toEntity();
        Domiciliario guardado = domiciliarioService.guardar(domiciliario);
        return ResponseEntity.status(HttpStatus.CREATED).body(DomiciliarioDto.fromEntity(guardado));
    }

    @Operation(summary = "Actualizar información de un domiciliario")
    @PutMapping({ "/update", "/update/{id}", "/{id}" })
    public ResponseEntity<DomiciliarioDto> actualizarDomiciliario(
            @PathVariable(name = "id", required = false) Long id,
            @RequestBody DomiciliarioDto dto) {
        Long targetId = (id != null) ? id : (dto != null ? dto.getId() : null);
        log.info("Petición REST: actualizando domiciliario con ID: {}", targetId);
        Domiciliario domiciliario = (dto != null) ? dto.toEntity() : null;
        Domiciliario actualizado = domiciliarioService.actualizar(targetId, domiciliario);
        return ResponseEntity.ok(DomiciliarioDto.fromEntity(actualizado));
    }

    @Operation(summary = "Actualizar o alternar la disponibilidad de un domiciliario")
    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<DomiciliarioDto> cambiarDisponibilidad(
            @PathVariable("id") Long id,
            @RequestParam(name = "disponible", required = false) Boolean disponible) {
        log.info("Petición REST: actualizar disponibilidad para domiciliario ID: {} a {}", id, disponible);
        Domiciliario actualizado = domiciliarioService.actualizarDisponibilidad(id, disponible);
        return ResponseEntity.ok(DomiciliarioDto.fromEntity(actualizado));
    }

    @Operation(summary = "Eliminar permanentemente un domiciliario")
    @DeleteMapping({ "/delete/{id}", "/{id}" })
    public ResponseEntity<Void> eliminarDomiciliario(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminando domiciliario con ID: {}", id);
        domiciliarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
