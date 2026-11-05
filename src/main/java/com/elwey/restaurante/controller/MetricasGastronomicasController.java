package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.MetricasGastronomicasDto;
import com.elwey.restaurante.dto.PlatoPopularDto;
import com.elwey.restaurante.service.MetricasGastronomicasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para consultar analítica del negocio gastronómico,
 * KPIs comerciales y tendencias de popularidad de platos en vivo.
 */
@Tag(name = "Métricas Gastronómicas", description = "Endpoints de analítica y monitoreo de negocio para el restaurante El Wey")
@RestController
@RequestMapping("/metricas-gastronomicas")
@CrossOrigin(origins = "http://localhost:4200")
public class MetricasGastronomicasController {

    @Autowired
    private MetricasGastronomicasService metricasService;

    @Operation(summary = "Obtener consolidado de métricas comerciales y operativas en tiempo real")
    @GetMapping("/resumen")
    public ResponseEntity<MetricasGastronomicasDto> obtenerResumenMetricas() {
        MetricasGastronomicasDto resumen = metricasService.obtenerResumenMetricas();
        return ResponseEntity.ok(resumen);
    }

    @Operation(summary = "Obtener ranking y porcentaje de popularidad de los platos del menú")
    @GetMapping("/tendencias")
    public ResponseEntity<List<PlatoPopularDto>> obtenerTendenciasPlatos() {
        List<PlatoPopularDto> tendencias = metricasService.obtenerPlatosMasPopulares();
        return ResponseEntity.ok(tendencias);
    }
}
