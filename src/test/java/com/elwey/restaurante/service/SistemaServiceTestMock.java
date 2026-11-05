package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.SistemaDiagnosticoDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * Pruebas unitarias para el servicio de diagnóstico del sistema.
 * Sigue la doctrina del profesor Angarita:
 * - @ExtendWith(MockitoExtension.class)
 * - @ActiveProfiles("test")
 * - Patrón Arrange - Act - Assert con AssertJ
 * - Nomenclatura [componente]_[metodo]_[lo_que_retorna]
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class SistemaServiceTestMock {

    @InjectMocks
    private SistemaServiceImpl sistemaService;

    /**
     * Prueba [DIAGNOSTICO]: Obtener las métricas operativas y de salud de la JVM.
     * Retorna un DTO con estado UP y valores de memoria coherentes.
     */
    @Test
    public void sistemaService_obtenerDiagnostico_retornaMetricasJvmValidas() {
        // Arrange (no requiere mocks externos pues lee Runtime)

        // Act
        SistemaDiagnosticoDto dto = sistemaService.obtenerDiagnostico();

        // Assert
        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getEstado()).isEqualTo("UP");
        Assertions.assertThat(dto.getJavaVersion()).isNotBlank();
        Assertions.assertThat(dto.getSistemaOperativo()).isNotBlank();
        Assertions.assertThat(dto.getMemoriaTotalMb()).isPositive();
        Assertions.assertThat(dto.getMemoriaMaximaMb()).isPositive();
        Assertions.assertThat(dto.getProcesadoresDisponibles()).isPositive();
        Assertions.assertThat(dto.getTimestamp()).isNotBlank();
    }
}
