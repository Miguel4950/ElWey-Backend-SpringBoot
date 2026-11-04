package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.RestauranteInfoDto;
import com.elwey.restaurante.service.RestauranteInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la consulta de información general, horarios y cobertura del restaurante.
 */
@Slf4j
@Tag(name = "Restaurante", description = "Endpoints informativos públicos del restaurante El Wey")
@RestController
@RequestMapping("/restaurante")
@CrossOrigin(origins = "http://localhost:4200")
public class RestauranteInfoController {

    @Autowired
    private RestauranteInfoService restauranteInfoService;

    @Operation(summary = "Obtener información general y horario del restaurante",
               description = "Retorna el nombre, contacto, horarios, estado de apertura y zonas de cobertura")
    @GetMapping("/info")
    public ResponseEntity<RestauranteInfoDto> obtenerInformacion() {
        log.info("GET /restaurante/info invocado");
        RestauranteInfoDto info = restauranteInfoService.obtenerInformacion();
        return ResponseEntity.ok(info);
    }
}
