package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.SistemaDiagnosticoDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementación del servicio de diagnóstico del sistema.
 * Inspecciona métricas de la JVM en tiempo de ejecución.
 */
@Service
public class SistemaServiceImpl implements SistemaService {

    private static final long MB = 1024L * 1024L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public SistemaDiagnosticoDto obtenerDiagnostico() {
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory() / MB;
        long totalMemory = runtime.totalMemory() / MB;
        long maxMemory = runtime.maxMemory() / MB;
        int availableProcessors = runtime.availableProcessors();

        return SistemaDiagnosticoDto.builder()
                .estado("UP")
                .javaVersion(System.getProperty("java.version"))
                .sistemaOperativo(System.getProperty("os.name"))
                .memoriaLibreMb(freeMemory)
                .memoriaTotalMb(totalMemory)
                .memoriaMaximaMb(maxMemory)
                .procesadoresDisponibles(availableProcessors)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }
}
