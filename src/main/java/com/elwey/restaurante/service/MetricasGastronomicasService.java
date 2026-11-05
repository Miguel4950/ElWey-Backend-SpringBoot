package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.MetricasGastronomicasDto;
import com.elwey.restaurante.dto.PlatoPopularDto;

import java.util.List;

/**
 * Contrato de servicio para el cálculo y consulta de métricas de negocio y tendencias gastronómicas.
 */
public interface MetricasGastronomicasService {

    /**
     * Obtiene el resumen consolidado de KPIs operativos y comerciales en vivo.
     */
    MetricasGastronomicasDto obtenerResumenMetricas();

    /**
     * Obtiene el ranking de platos con mayor rotación y popularidad en las órdenes.
     */
    List<PlatoPopularDto> obtenerPlatosMasPopulares();
}
