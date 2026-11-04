package com.elwey.restaurante.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la transferencia de métricas operacionales del restaurante al administrador.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenEstadisticasDto {

    private Long totalPedidos;
    private Long pedidosCocinando;
    private Long pedidosListos;
    private Long pedidosEnviados;
    private Long pedidosEntregados;
    private Double totalVentasEntregadas;
    private Long totalComidasActivas;
    private Long domiciliariosDisponibles;
    private Long totalDomiciliarios;
}
