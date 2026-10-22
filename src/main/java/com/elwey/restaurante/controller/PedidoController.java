package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.dto.PedidoDto;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.service.PedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({ "/api/pedidos", "/pedidos" })
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    // http://localhost:8080/api/pedidos
    @GetMapping({ "", "/", "/all" })
    public ResponseEntity<List<PedidoDto>> mostrarPedidos() {
        log.info("Petición REST: consultando lista completa de pedidos");
        List<PedidoDto> dtos = pedidoService.searchAll().stream()
                .map(PedidoDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // http://localhost:8080/api/pedidos/1
    @GetMapping({ "/{id}", "/find/{id}" })
    public ResponseEntity<PedidoDto> mostrarPedidoPorId(@PathVariable("id") Long id) {
        log.info("Petición REST: consultando detalle de pedido con ID: {}", id);
        Pedido pedido = pedidoService.searchById(id);
        return ResponseEntity.ok(PedidoDto.fromEntity(pedido));
    }

    // http://localhost:8080/api/pedidos/cliente/1
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoDto>> mostrarPedidosPorCliente(@PathVariable("clienteId") Long clienteId) {
        log.info("Petición REST: consultando pedidos para cliente ID: {}", clienteId);
        List<PedidoDto> dtos = pedidoService.findByCliente(clienteId).stream()
                .map(PedidoDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // http://localhost:8080/api/pedidos/1
    @DeleteMapping({ "/delete/{id}", "/{id}" })
    public ResponseEntity<Void> eliminarPedido(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminando pedido con ID: {}", id);
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // http://localhost:8080/api/pedidos/ordenar
    @PostMapping({ "", "/", "/ordenar", "/add" })
    public ResponseEntity<PedidoDto> ordenarPlatoConAdicionales(@RequestBody CrearPedidoDto orderRequest) {
        if (orderRequest == null || (orderRequest.getComidaId() == null && (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()))) {
            return ResponseEntity.badRequest().build();
        }
        log.info("Petición REST: creando pedido en BD (comidaId: {}, items: {})",
                orderRequest.getComidaId(),
                orderRequest.getItems() != null ? orderRequest.getItems().size() : 0);
        Pedido nuevo = pedidoService.crearPedido(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoDto.fromEntity(nuevo));
    }
}
