package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.ClienteDto;
import com.elwey.restaurante.dto.ComidaDto;
import com.elwey.restaurante.dto.LoginRequestDto;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.service.ClienteService;
import com.elwey.restaurante.service.ComidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para endpoints directos y de compatibilidad en la raíz del servidor.
 */
@Tag(name = "Home y Accesos Rápidos", description = "Endpoints de compatibilidad para inicio, login rápido y vistas administrativas")
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class HomeController {

    @Autowired
    private ComidaService service;

    @Autowired
    private ClienteService clienteService;

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Operation(summary = "Consultar comidas destacadas para la página de inicio")
    @GetMapping({ "/", "/index", "/home" })
    public ResponseEntity<List<ComidaDto>> inicio() {
        log.info("Petición REST: consultar comidas para inicio");
        List<ComidaDto> dtos = service.listarTodas().stream()
                .map(ComidaDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Inicio de sesión directo en la ruta raíz")
    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequestDto loginRequest) {
        log.info("Petición REST login para: {}", loginRequest != null ? loginRequest.getCorreo() : "null");
        if (loginRequest == null || loginRequest.getCorreo() == null || loginRequest.getContrasena() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe proporcionar correo y contraseña."));
        }
        try {
            ClienteDto cliente = clienteService.autenticar(loginRequest.getCorreo(), loginRequest.getContrasena());
            return ResponseEntity.ok(cliente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Credenciales inválidas. Verifica tu correo y contraseña."));
        }
    }

    @Operation(summary = "Consultar productos administrativos con filtro de texto")
    @GetMapping("/admin/productos")
    public ResponseEntity<List<ComidaDto>> adminProductos(@RequestParam(name = "q", required = false) String query) {
        log.info("Petición REST: consultar productos administrativos");
        List<ComidaDto> dtos = service.buscarPorNombre(query).stream()
                .map(ComidaDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Consultar listado completo de clientes registrados")
    @GetMapping({ "/admin/vertodo", "/admin/clientes" })
    public ResponseEntity<List<ClienteDto>> verTodosLosClientes() {
        log.info("Petición REST: consultar todos los clientes");
        List<ClienteDto> dtos = clienteService.listarTodos().stream()
                .map(ClienteDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
