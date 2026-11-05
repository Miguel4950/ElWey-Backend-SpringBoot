package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.SistemaDiagnosticoDto;

/**
 * Interfaz para el diagnóstico y monitoreo de la plataforma backend.
 */
public interface SistemaService {

    SistemaDiagnosticoDto obtenerDiagnostico();
}
