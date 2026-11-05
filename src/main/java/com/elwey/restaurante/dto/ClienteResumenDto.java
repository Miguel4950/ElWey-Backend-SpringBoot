package com.elwey.restaurante.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para transferir el resumen consolidado de compras y actividad de un cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResumenDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long clienteId;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String direccion;
    private int totalPedidos;
    private double totalGastado;
    private String ultimoPedidoFecha;
}
