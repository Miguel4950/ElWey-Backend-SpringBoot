package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.entities.ItemPedido;
import com.elwey.restaurante.entities.ItemPedidoAdicional;
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
public class ItemPedidoDto {

    private Long id;
    private Long comidaId;
    private String comidaNombre;
    private String categoriaNombre;
    private Double comidaPrecio;
    private Integer cantidad;
    private Integer cantidadAdicionales;
    private Double subtotalComida;
    private Double subtotalAdicionales;
    private Double subtotal;
    private List<AdicionalSeleccionadoDto> adicionales;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdicionalSeleccionadoDto {
        private Long id;
        private String nombre;
        private Double precio;
        private Integer cantidad;
        private Double subtotal;
    }

    public static ItemPedidoDto fromEntity(ItemPedido item) {
        if (item == null) return null;

        Long cId = (item.getComida() != null) ? item.getComida().getId() : null;
        String cNombre = (item.getComida() != null) ? item.getComida().getNombre() : "Plato";
        String catNombre = (item.getComida() != null && item.getComida().getCategoria() != null)
                ? item.getComida().getCategoria().getNombre()
                : "General";
        Double cPrecio = (item.getComida() != null) ? item.getComida().getPrecio() : 0.0;

        List<AdicionalSeleccionadoDto> adicionalesDto = new ArrayList<>();
        if (item.getAdicionales() != null) {
            for (ItemPedidoAdicional ipa : item.getAdicionales()) {
                if (ipa != null && ipa.getAdicional() != null) {
                    Adicional a = ipa.getAdicional();
                    adicionalesDto.add(AdicionalSeleccionadoDto.builder()
                            .id(a.getId())
                            .nombre(a.getNombre())
                            .precio(a.getPrecio())
                            .cantidad(ipa.getCantidad())
                            .subtotal(ipa.getSubtotal())
                            .build());
                }
            }
        }

        return ItemPedidoDto.builder()
                .id(item.getId())
                .comidaId(cId)
                .comidaNombre(cNombre)
                .categoriaNombre(catNombre)
                .comidaPrecio(cPrecio)
                .cantidad(item.getCantidad())
                .cantidadAdicionales(item.getCantidadAdicionales())
                .subtotalComida(item.getSubtotalComida())
                .subtotalAdicionales(item.getSubtotalAdicionales())
                .subtotal(item.getSubtotal())
                .adicionales(adicionalesDto)
                .build();
    }
}
