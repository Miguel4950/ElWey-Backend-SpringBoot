package com.elwey.restaurante.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta consolidada con el resultado del canje y cálculo del cupón.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta de validación de un cupón promocional")
public class ValidarCuponResponseDto {

    @Schema(description = "Indica si el cupón es válido para la compra", example = "true")
    private Boolean valido;

    @Schema(description = "Código del cupón evaluado", example = "ELWEY10")
    private String codigo;

    @Schema(description = "Monto descontado en pesos COP", example = "5000")
    private Integer descuentoCalculado;

    @Schema(description = "Total final a pagar tras aplicar el cupón", example = "45000")
    private Integer totalFinal;

    @Schema(description = "Mensaje explicativo o de marketing para el comensal", example = "¡Cupón ELWEY10 aplicado con éxito!")
    private String mensaje;
}
