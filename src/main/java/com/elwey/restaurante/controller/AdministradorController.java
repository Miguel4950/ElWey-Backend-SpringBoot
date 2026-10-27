package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.AdministradorDto;
import com.elwey.restaurante.dto.LoginRequestDto;
import com.elwey.restaurante.service.AdministradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la autenticación de Administrador en el sistema "El Wey".
 */
@Slf4j
@Tag(name = "Administradores", description = "Endpoint de autenticación para Administrador")
@RestController
@RequestMapping({ "/administrador", "/admin" })
@CrossOrigin(origins = "http://localhost:4200")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @Operation(summary = "Login para administrador (por usuario)")
    @PostMapping("/login")
    public ResponseEntity<AdministradorDto> login(@RequestBody LoginRequestDto loginRequest) {
        log.info("Petición REST: autenticar administrador {}", loginRequest != null ? loginRequest.getCorreo() : "null");
        String usuario = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;

        AdministradorDto respuesta = administradorService.autenticar(usuario, contrasena);
        return ResponseEntity.ok(respuesta);
    }
}
