package com.elwey.restaurante.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para la transferencia de métricas de diagnóstico y salud del servidor JVM.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SistemaDiagnosticoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String estado;
    private String javaVersion;
    private String sistemaOperativo;
    private long memoriaLibreMb;
    private long memoriaTotalMb;
    private long memoriaMaximaMb;
    private int procesadoresDisponibles;
    private String timestamp;
}
