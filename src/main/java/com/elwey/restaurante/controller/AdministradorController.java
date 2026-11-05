package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.AdministradorDto;
import com.elwey.restaurante.dto.LoginRequestDto;
import com.elwey.restaurante.service.AdministradorService;
import com.elwey.restaurante.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

/**
 * Controlador REST para la autenticación y reportes de Administrador en el sistema "El Wey".
 */
@Slf4j
@Tag(name = "Administradores", description = "Endpoints de autenticación y reportería para Administrador")
@RestController
@RequestMapping({ "/administrador", "/admin" })
@CrossOrigin(origins = "http://localhost:4200")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private ReporteService reporteService;

    @Operation(summary = "Login para administrador (por usuario)")
    @PostMapping("/login")
    public ResponseEntity<AdministradorDto> login(@RequestBody LoginRequestDto loginRequest) {
        log.info("Petición REST: autenticar administrador {}", loginRequest != null ? loginRequest.getCorreo() : "null");
        String usuario = loginRequest != null ? loginRequest.getCorreo() : null;
        String contrasena = loginRequest != null ? loginRequest.getContrasena() : null;

        AdministradorDto respuesta = administradorService.autenticar(usuario, contrasena);
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Exportar histórico consolidado de pedidos en formato CSV para Excel")
    @GetMapping("/reportes/pedidos-csv")
    public ResponseEntity<byte[]> exportarReportePedidosCsv() {
        log.info("Petición REST: exportar reporte consolidado de pedidos en formato CSV");
        byte[] csvBytes = reporteService.generarReportePedidosCsv();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("reporte_pedidos_elwey.csv")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
}
