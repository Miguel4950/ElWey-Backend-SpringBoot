package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.dto.ItemPedidoDto;
import com.elwey.restaurante.entities.ItemPedido;
import com.elwey.restaurante.entities.Pedido;
import java.util.List;

/**
 * Interfaz de la capa de servicio para Pedidos.
 * Administra el ciclo de vida, cálculo de subtotales y totales, flujo de estados y consulta de ítems.
 */
public interface PedidoService {

    List<Pedido> searchAll();

    Pedido searchById(Long id);

    List<Pedido> findByCliente(Long clienteId);

    List<ItemPedidoDto> obtenerItemsPedido(Long pedidoId);

    Pedido crearPedido(CrearPedidoDto dto);

    Pedido actualizarEstado(Long pedidoId, String nuevoEstado, Long domiciliarioId);

    void delete(Long id);

    Double calcularSubtotal(ItemPedido item);

    Double calcularTotal(Pedido pedido);

    void calcularTotales(Pedido pedido);
}
