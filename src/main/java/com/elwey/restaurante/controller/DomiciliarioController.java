package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.LoginRequestDto;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.service.DomiciliarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Domiciliarios y su disponibilidad de reparto.
 */
@Tag(name = "Domiciliarios", description = "Endpoints para el personal de reparto y su disponibilidad")
@RestController
@RequestMapping("/api/domiciliarios")
@CrossOrigin(origins = "http://localhost:4200")
public class DomiciliarioController {

    @Autowired
    private DomiciliarioService domiciliarioService;

    private static final Logger log = LoggerFactory.getLogger(DomiciliarioController.class);

    @Operation(summary = "Autenticar domiciliario mediante cédula o celular y contraseña")
    @PostMapping({ "/login", "/auth/login" })
    public ResponseEntity<Domiciliario> login(@RequestBody LoginRequestDto loginRequest) {
        String identificador = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;
        log.info("Petición REST: login domiciliario con identificador: {}", identificador);
        Domiciliario domiciliario = domiciliarioService.autenticar(identificador, contrasena);
        return ResponseEntity.ok(domiciliario);
    }

    @Operation(summary = "Listar todos los domiciliarios registrados")
    @GetMapping({ "", "/" })
    public ResponseEntity<List<Domiciliario>> listarTodos() {
        log.info("Petición REST: listar todos los domiciliarios");
        return ResponseEntity.ok(domiciliarioService.searchAll());
    }

    @Operation(summary = "Listar únicamente los domiciliarios disponibles para asignación")
    @GetMapping("/disponibles")
    public ResponseEntity<List<Domiciliario>> listarDisponibles() {
        log.info("Petición REST: listar domiciliarios disponibles");
        return ResponseEntity.ok(domiciliarioService.listarDisponibles());
    }

    @Operation(summary = "Obtener un domiciliario por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<Domiciliario> obtenerPorId(@PathVariable("id") Long id) {
        log.info("Petición REST: obtener domiciliario ID: {}", id);
        return ResponseEntity.ok(domiciliarioService.searchById(id));
    }

    @Operation(summary = "Actualizar o alternar la disponibilidad de un domiciliario")
    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<Domiciliario> cambiarDisponibilidad(
            @PathVariable("id") Long id,
            @RequestParam(name = "disponible", required = false) Boolean disponible) {
        log.info("Petición REST: actualizar disponibilidad para domiciliario ID: {} a {}", id, disponible);
        Domiciliario actualizado = domiciliarioService.actualizarDisponibilidad(id, disponible);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Registrar un nuevo domiciliario en el sistema")
    @PostMapping({ "", "/" })
    public ResponseEntity<Domiciliario> crearDomiciliario(@RequestBody Domiciliario domiciliario) {
        log.info("Petición REST: registrar domiciliario: {}", domiciliario.getNombre());
        Domiciliario nuevo = domiciliarioService.save(domiciliario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(summary = "Actualizar datos de un domiciliario")
    @PutMapping("/{id}")
    public ResponseEntity<Domiciliario> actualizarDomiciliario(
            @PathVariable("id") Long id,
            @RequestBody Domiciliario domiciliario) {
        log.info("Petición REST: actualizar domiciliario ID: {}", id);
        Domiciliario actualizado = domiciliarioService.actualizar(id, domiciliario);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar un domiciliario del sistema")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDomiciliario(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminar domiciliario ID: {}", id);
        domiciliarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
