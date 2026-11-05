package com.elwey.restaurante.service;

/**
 * Servicio de negocio para la generación y exportación de reportes operacionales y contables.
 */
public interface ReporteService {

    /**
     * Genera el reporte consolidado de pedidos en formato CSV codificado en UTF-8 con BOM
     * para compatibilidad nativa con Microsoft Excel.
     *
     * @return Arreglo de bytes del archivo CSV.
     */
    byte[] generarReportePedidosCsv();
}
