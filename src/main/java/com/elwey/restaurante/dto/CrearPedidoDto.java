package com.elwey.restaurante.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrearPedidoDto {

    private Long clienteId;
    private List<ItemPedidoRequestDto> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemPedidoRequestDto {
        private Long comidaId;
        private Integer cantidad;
        private List<AdicionalCantidadDto> adicionales;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdicionalCantidadDto {
        private Long adicionalId;
        private Integer cantidad;
    }
}
