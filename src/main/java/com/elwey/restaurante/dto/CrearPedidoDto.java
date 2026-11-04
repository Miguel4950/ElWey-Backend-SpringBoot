package com.elwey.restaurante.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrearPedidoDto {

    @NotNull(message = "El identificador del cliente es obligatorio")
    private Long clienteId;

    @NotEmpty(message = "El pedido debe contener al menos un producto")
    @Valid
    private List<ItemPedidoRequestDto> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemPedidoRequestDto {

        @NotNull(message = "El identificador del plato es obligatorio")
        private Long comidaId;

        @NotNull(message = "La cantidad del plato es obligatoria")
        @Min(value = 1, message = "La cantidad mínima por plato debe ser al menos 1")
        private Integer cantidad;

        @Valid
        private List<AdicionalCantidadDto> adicionales;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdicionalCantidadDto {

        @NotNull(message = "El identificador del adicional es obligatorio")
        private Long adicionalId;

        @NotNull(message = "La cantidad del adicional es obligatoria")
        @Min(value = 1, message = "La cantidad mínima del adicional debe ser al menos 1")
        private Integer cantidad;
    }
}
