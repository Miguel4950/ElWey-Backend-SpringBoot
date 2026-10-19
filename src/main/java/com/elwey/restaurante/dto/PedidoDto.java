package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.ItemPedido;
import com.elwey.restaurante.entities.Pedido;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PedidoDto {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long id;
    private String estado;
    private String fechaCreacion;

    private Long clienteId;
    private String clienteNombre;
    private String clienteCorreo;
    private String clienteTelefono;
    private String clienteDireccion;

    private String domiciliarioNombre;
    private String domiciliarioCelular;

    private Integer cantidadTotalProductos;
    private Integer cantidadTotalAdicionales;
    private Double subtotalComida;
    private Double subtotalAdicionales;
    private Double total;

    @Builder.Default
    private List<ItemPedidoDto> items = new ArrayList<>();

    public static PedidoDto fromEntity(Pedido pedido) {
        if (pedido == null) return null;

        String fechaStr = (pedido.getFechaCreacion() != null)
                ? pedido.getFechaCreacion().format(FORMATTER)
                : "";

        Long cliId = (pedido.getCliente() != null) ? pedido.getCliente().getId() : null;
        String cliNombre = "Cliente Invitado";
        String cliCorreo = "";
        String cliTelefono = "";
        String cliDireccion = "";

        if (pedido.getCliente() != null) {
            cliNombre = (pedido.getCliente().getNombreCompleto() != null && !pedido.getCliente().getNombreCompleto().isBlank())
                    ? pedido.getCliente().getNombreCompleto()
                    : pedido.getCliente().getNombre();
            cliCorreo = pedido.getCliente().getEmail();
            cliTelefono = pedido.getCliente().getTelefono();
            cliDireccion = pedido.getCliente().getDireccion();
        }

        String domNombre = (pedido.getDomiciliario() != null) ? pedido.getDomiciliario().getNombre() : null;
        String domCelular = (pedido.getDomiciliario() != null) ? pedido.getDomiciliario().getCelular() : null;

        List<ItemPedidoDto> itemsDto = new ArrayList<>();
        int totalProductos = 0;
        int totalAdicionales = 0;
        double subComida = 0.0;
        double subAdicionales = 0.0;

        if (pedido.getItems() != null) {
            for (ItemPedido item : pedido.getItems()) {
                ItemPedidoDto itemDto = ItemPedidoDto.fromEntity(item);
                if (itemDto != null) {
                    itemsDto.add(itemDto);
                    totalProductos += (itemDto.getCantidad() != null) ? itemDto.getCantidad() : 0;
                    totalAdicionales += (itemDto.getCantidadAdicionales() != null) ? itemDto.getCantidadAdicionales() : 0;
                    subComida += (itemDto.getSubtotalComida() != null) ? itemDto.getSubtotalComida() : 0.0;
                    subAdicionales += (itemDto.getSubtotalAdicionales() != null) ? itemDto.getSubtotalAdicionales() : 0.0;
                }
            }
        }

        double totalCalculado = subComida + subAdicionales;

        return PedidoDto.builder()
                .id(pedido.getId())
                .estado(pedido.getEstado())
                .fechaCreacion(fechaStr)
                .clienteId(cliId)
                .clienteNombre(cliNombre)
                .clienteCorreo(cliCorreo)
                .clienteTelefono(cliTelefono)
                .clienteDireccion(cliDireccion)
                .domiciliarioNombre(domNombre)
                .domiciliarioCelular(domCelular)
                .cantidadTotalProductos(totalProductos)
                .cantidadTotalAdicionales(totalAdicionales)
                .subtotalComida(subComida)
                .subtotalAdicionales(subAdicionales)
                .total(totalCalculado)
                .items(itemsDto)
                .build();
    }
}
