package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.LoginRequestDto;
import com.elwey.restaurante.entities.Operador;
import com.elwey.restaurante.service.OperadorService;
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
 * Controlador REST para el módulo administrativo y de gestión de Operadores de cocina/despacho.
 */
@Tag(name = "Operadores", description = "Endpoints para el CRUD de operadores del restaurante")
@RestController
@RequestMapping("/api/operadores")
@CrossOrigin(origins = "http://localhost:4200")
public class OperadorController {

    @Autowired
    private OperadorService operadorService;

    private static final Logger log = LoggerFactory.getLogger(OperadorController.class);

    @Operation(summary = "Autenticar operador de cocina o despachos")
    @PostMapping({ "/login", "/auth/login" })
    public ResponseEntity<Operador> login(@RequestBody LoginRequestDto loginRequest) {
        String usuario = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;
        log.info("Petición REST: login operador con usuario: {}", usuario);
        Operador op = operadorService.autenticar(usuario, contrasena);
        return ResponseEntity.ok(op);
    }

    @Operation(summary = "Listar todos los operadores registrados")
    @GetMapping({ "", "/" })
    public ResponseEntity<List<Operador>> listarTodos() {
        log.info("Petición REST: listar todos los operadores");
        return ResponseEntity.ok(operadorService.listarTodos());
    }

    @Operation(summary = "Obtener un operador por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<Operador> obtenerPorId(@PathVariable("id") Long id) {
        log.info("Petición REST: obtener operador con ID: {}", id);
        return ResponseEntity.ok(operadorService.obtenerPorId(id));
    }

    @Operation(summary = "Registrar un nuevo operador en el sistema")
    @PostMapping({ "", "/" })
    public ResponseEntity<Operador> crearOperador(@RequestBody Operador operador) {
        log.info("Petición REST: crear nuevo operador: {}", operador.getUsuario());
        Operador guardado = operadorService.guardar(operador);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @Operation(summary = "Actualizar información de un operador existente")
    @PutMapping("/{id}")
    public ResponseEntity<Operador> actualizarOperador(
            @PathVariable("id") Long id,
            @RequestBody Operador operador) {
        log.info("Petición REST: actualizar operador con ID: {}", id);
        Operador actualizado = operadorService.actualizar(id, operador);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar un operador por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOperador(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminar operador con ID: {}", id);
        operadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
