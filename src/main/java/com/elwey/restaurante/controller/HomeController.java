package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.ClienteDto;
import com.elwey.restaurante.dto.ComidaDto;
import com.elwey.restaurante.dto.LoginRequestDto;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.service.ClienteService;
import com.elwey.restaurante.service.ComidaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class HomeController {

    @Autowired
    private ComidaService service;

    @Autowired
    private ClienteService clienteService;

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    /**
     * GET /, GET /index, GET /home
     * Retorna la colección de platos destacados para la página de inicio en formato JSON.
     */
    @GetMapping({ "/", "/index", "/home" })
    public ResponseEntity<List<ComidaDto>> inicio() {
        log.info("Petición REST: consultar comidas para inicio");
        List<ComidaDto> dtos = service.listarTodas().stream()
                .map(ComidaDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * POST /login
     * Valida las credenciales del usuario y retorna el objeto Cliente autenticado.
     */
    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequestDto loginRequest) {
        log.info("Petición REST login para: {}", loginRequest != null ? loginRequest.getCorreo() : "null");
        if (loginRequest == null || loginRequest.getCorreo() == null || loginRequest.getContrasena() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe proporcionar correo y contraseña."));
        }
        try {
            Cliente cliente = clienteService.autenticar(loginRequest.getCorreo(), loginRequest.getContrasena());
            return ResponseEntity.ok(ClienteDto.fromEntity(cliente));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Credenciales inválidas. Verifica tu correo y contraseña."));
        }
    }

    /**
     * POST /registro
     * Registra un nuevo cliente y retorna el objeto guardado en JSON.
     */
    @PostMapping("/registro")
    public ResponseEntity<ClienteDto> registrarCliente(@RequestBody ClienteDto dto) {
        log.info("Petición REST registro para cliente: {}", dto != null ? dto.getCorreo() : "null");
        Cliente cliente = dto.toEntity();
        Cliente guardado = clienteService.guardar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteDto.fromEntity(guardado));
    }

    /**
     * GET /admin/productos
     * Retorna listado de comidas con filtro opcional por query.
     */
    @GetMapping("/admin/productos")
    public ResponseEntity<List<ComidaDto>> adminProductos(@RequestParam(name = "q", required = false) String query) {
        log.info("Petición REST: consultar productos administrativos");
        List<ComidaDto> dtos = service.buscarPorNombre(query).stream()
                .map(ComidaDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /admin/vertodo, GET /admin/clientes
     * Retorna el listado completo de clientes registrados.
     */
    @GetMapping({ "/admin/vertodo", "/admin/clientes" })
    public ResponseEntity<List<ClienteDto>> verTodosLosClientes() {
        log.info("Petición REST: consultar todos los clientes");
        List<ClienteDto> dtos = clienteService.listarTodos().stream()
                .map(ClienteDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}