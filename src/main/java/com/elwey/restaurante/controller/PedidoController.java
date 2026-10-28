package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.ActualizarEstadoPedidoDto;
import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.dto.ItemPedidoDto;
import com.elwey.restaurante.dto.PedidoDto;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Pedidos.
 */
@Tag(name = "Pedidos", description = "Endpoints para la gestión, ordenamiento, monitoreo y cambio de estado de pedidos")
@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    @Operation(summary = "Listar todos los pedidos registrados en el restaurante")
    @GetMapping
    public ResponseEntity<List<PedidoDto>> mostrarPedidos() {
        log.info("Petición REST: consultando lista completa de pedidos");
        List<PedidoDto> dtos = pedidoService.searchAll().stream()
                .map(PedidoDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener el detalle consolidado de un pedido por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> mostrarPedidoPorId(@PathVariable("id") Long id) {
        log.info("Petición REST: consultando detalle de pedido con ID: {}", id);
        Pedido pedido = pedidoService.searchById(id);
        return ResponseEntity.ok(PedidoDto.fromEntity(pedido));
    }

    @Operation(summary = "Consultar el historial de pedidos asociados a un cliente")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoDto>> mostrarPedidosPorCliente(@PathVariable("clienteId") Long clienteId) {
        log.info("Petición REST: consultando pedidos para cliente ID: {}", clienteId);
        List<PedidoDto> dtos = pedidoService.findByCliente(clienteId).stream()
                .map(PedidoDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Consultar los pedidos asignados a un domiciliario")
    @GetMapping("/domiciliario/{domiciliarioId}")
    public ResponseEntity<List<PedidoDto>> mostrarPedidosPorDomiciliario(@PathVariable("domiciliarioId") Long domiciliarioId) {
        log.info("Petición REST: consultando pedidos para domiciliario ID: {}", domiciliarioId);
        List<PedidoDto> dtos = pedidoService.findByDomiciliario(domiciliarioId).stream()
                .map(PedidoDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener la lista detallada de platos e ingredientes adicionales (items) de un pedido")
    @GetMapping("/{id}/items")
    public ResponseEntity<List<ItemPedidoDto>> obtenerItemsDePedido(@PathVariable("id") Long id) {
        log.info("Petición REST: consultando items detallados del pedido ID: {}", id);
        return ResponseEntity.ok(pedidoService.obtenerItemsPedido(id));
    }

    @Operation(summary = "Actualizar el estado del pedido y gestionar domiciliario")
    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoDto> actualizarEstadoPedido(
            @PathVariable("id") Long id,
            @RequestBody ActualizarEstadoPedidoDto dto) {
        log.info("Petición REST: actualizando estado de pedido ID: {} a '{}' con domiciliario ID: {}",
                id, dto.getEstado(), dto.getDomiciliarioId());
        Pedido actualizado = pedidoService.actualizarEstado(id, dto.getEstado(), dto.getDomiciliarioId());
        return ResponseEntity.ok(PedidoDto.fromEntity(actualizado));
    }

    @Operation(summary = "Crear un nuevo pedido en el sistema")
    @PostMapping("/ordenar")
    public ResponseEntity<PedidoDto> ordenarPlatoConAdicionales(@RequestBody CrearPedidoDto orderRequest) {
        if (orderRequest == null || orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        log.info("Petición REST: creando pedido en BD para cliente ID: {} (items: {})",
                orderRequest.getClienteId(), orderRequest.getItems().size());
        Pedido nuevo = pedidoService.crearPedido(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoDto.fromEntity(nuevo));
    }

    @Operation(summary = "Eliminar un pedido por su ID")
    @DeleteMapping({ "/{id}", "/delete/{id}" })
    public ResponseEntity<Void> eliminarPedido(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminando pedido con ID: {}", id);
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
