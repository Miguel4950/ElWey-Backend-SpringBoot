package com.elwey.restaurante.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrearPedidoDto {

    private Long comidaId;

    @Builder.Default
    private Integer cantidad = 1;

    @Builder.Default
    private List<AdicionalCantidadDto> adicionales = new ArrayList<>();

    private Long clienteId;

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
