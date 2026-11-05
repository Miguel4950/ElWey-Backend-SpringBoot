package com.elwey.restaurante.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload para solicitar la validación y cálculo de un cupón de descuento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload para solicitar la validación de un cupón")
public class ValidarCuponRequestDto {

    @Schema(description = "Código del cupón a validar", example = "ELWEY10")
    private String codigo;

    @Schema(description = "Monto total del pedido en COP antes de aplicar descuento", example = "50000")
    private Integer montoPedido;
}
