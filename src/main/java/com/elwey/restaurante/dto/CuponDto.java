package com.elwey.restaurante.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la transferencia y consulta de cupones de descuento promocionales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO representativo de un cupón de descuento promocional")
public class CuponDto {

    @Schema(description = "Código alfanumérico del cupón", example = "ELWEY10")
    private String codigo;

    @Schema(description = "Descripción comercial del beneficio", example = "10% de descuento en tu orden")
    private String descripcion;

    @Schema(description = "Tipo de descuento (porcentaje o fijo)", example = "porcentaje")
    private String tipo;

    @Schema(description = "Valor numérico del descuento (% o COP)", example = "10")
    private Integer valor;

    @Schema(description = "Monto mínimo de compra en COP requerido", example = "20000")
    private Integer compraMinima;

    @Schema(description = "Indica si el cupón está vigente y activo", example = "true")
    private Boolean activo;
}
