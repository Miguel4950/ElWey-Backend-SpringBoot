package com.elwey.restaurante.dto;

import lombok.*;

/**
 * DTO para la actualización de estado y asignación de domiciliario en un pedido.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarEstadoPedidoDto {
    private String estado;
    private Long domiciliarioId;
}
