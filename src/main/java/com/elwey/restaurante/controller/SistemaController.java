package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.SistemaDiagnosticoDto;
import com.elwey.restaurante.service.SistemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el monitoreo y diagnóstico de salud del servidor "El Wey".
 */
@Tag(name = "Sistema", description = "Endpoints de monitoreo, diagnóstico y salud del servidor")
@RestController
@RequestMapping("/sistema")
@CrossOrigin(origins = "http://localhost:4200")
public class SistemaController {

    private static final Logger log = LoggerFactory.getLogger(SistemaController.class);

    @Autowired
    private SistemaService sistemaService;

    @Operation(summary = "Obtener diagnóstico de salud y métricas de la JVM")
    @GetMapping("/diagnostico")
    public ResponseEntity<SistemaDiagnosticoDto> obtenerDiagnostico() {
        log.info("Petición REST: consultar diagnóstico y estado del sistema");
        SistemaDiagnosticoDto dto = sistemaService.obtenerDiagnostico();
        return ResponseEntity.ok(dto);
    }
}
