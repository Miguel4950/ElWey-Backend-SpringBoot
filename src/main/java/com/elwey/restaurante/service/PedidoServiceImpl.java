package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.dto.ItemPedidoDto;
import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.ItemPedido;
import com.elwey.restaurante.entities.ItemPedidoAdicional;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.errors.ClienteNotFoundException;
import com.elwey.restaurante.errors.ComidaNotFoundException;
import com.elwey.restaurante.errors.DomiciliarioNotFoundException;
import com.elwey.restaurante.errors.PedidoNotFoundException;
import com.elwey.restaurante.repository.AdicionalRepository;
import com.elwey.restaurante.repository.ClienteRepository;
import com.elwey.restaurante.repository.ComidaRepository;
import com.elwey.restaurante.repository.DomiciliarioRepository;
import com.elwey.restaurante.repository.ItemPedidoAdicionalRepository;
import com.elwey.restaurante.repository.ItemPedidoRepository;
import com.elwey.restaurante.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la capa de servicio para Pedido.
 * Controla la creación de órdenes, el cálculo de subtotales y totales en el
 * servicio,
 * el flujo de estados operativos y la asignación/liberación de domiciliarios.
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private ItemPedidoAdicionalRepository itemPedidoAdicionalRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> searchAll() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        pedidos.forEach(this::calcularTotales);
        return pedidos;
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido searchById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        calcularTotales(pedido);
        return pedido;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findByCliente(Long clienteId) {
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        pedidos.forEach(this::calcularTotales);
        return pedidos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemPedidoDto> obtenerItemsPedido(Long pedidoId) {
        Pedido pedido = searchById(pedidoId);
        return pedido.getItems().stream()
                .map(ItemPedidoDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public Pedido crearPedido(CrearPedidoDto dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ClienteNotFoundException(dto.getClienteId()));

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .estado("En preparación")
                .fechaCreacion(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        pedido = pedidoRepository.save(pedido);

        if (dto.getItems() != null) {
            for (CrearPedidoDto.ItemPedidoRequestDto itemReq : dto.getItems()) {
                Comida comida = comidaRepository.findById(itemReq.getComidaId())
                        .orElseThrow(() -> new ComidaNotFoundException(itemReq.getComidaId()));
                int cantidad = (itemReq.getCantidad() != null && itemReq.getCantidad() > 0) ? itemReq.getCantidad() : 1;

                ItemPedido item = ItemPedido.builder()
                        .pedido(pedido)
                        .comida(comida)
                        .cantidad(cantidad)
                        .adicionales(new ArrayList<>())
                        .build();
                item = itemPedidoRepository.save(item);

                if (itemReq.getAdicionales() != null) {
                    for (CrearPedidoDto.AdicionalCantidadDto adicReq : itemReq.getAdicionales()) {
                        if (adicReq.getAdicionalId() != null && adicReq.getCantidad() != null
                                && adicReq.getCantidad() > 0) {
                            Adicional adicional = adicionalRepository.findById(adicReq.getAdicionalId()).orElse(null);
                            if (adicional != null) {
                                ItemPedidoAdicional relacion = ItemPedidoAdicional.builder()
                                        .itemPedido(item)
                                        .adicional(adicional)
                                        .cantidad(adicReq.getCantidad())
                                        .build();
                                item.getAdicionales().add(itemPedidoAdicionalRepository.save(relacion));
                            }
                        }
                    }
                }
                pedido.getItems().add(item);
            }
        }

        calcularTotales(pedido);
        return pedido;
    }

    @Override
    @Transactional
    public Pedido actualizarEstado(Long pedidoId, String nuevoEstado, Long domiciliarioId) {
        Pedido pedido = searchById(pedidoId);

        if (nuevoEstado != null && !nuevoEstado.isBlank()) {
            pedido.setEstado(nuevoEstado.trim());
        }

        if (domiciliarioId != null) {
            Domiciliario dom = domiciliarioRepository.findById(domiciliarioId)
                    .orElseThrow(() -> new DomiciliarioNotFoundException(domiciliarioId));
            pedido.setDomiciliario(dom);
        }

        if ("En camino".equalsIgnoreCase(pedido.getEstado()) || "Enviado".equalsIgnoreCase(pedido.getEstado())) {
            if (pedido.getDomiciliario() == null) {
                List<Domiciliario> disponibles = domiciliarioRepository.findByDisponible(true);
                if (!disponibles.isEmpty()) {
                    pedido.setDomiciliario(disponibles.get(0));
                }
            }
            if (pedido.getDomiciliario() != null) {
                pedido.getDomiciliario().setDisponible(false);
                domiciliarioRepository.save(pedido.getDomiciliario());
            }
        } else if ("Entregado".equalsIgnoreCase(pedido.getEstado())) {
            pedido.setFechaEntrega(LocalDateTime.now());
            if (pedido.getDomiciliario() != null) {
                pedido.getDomiciliario().setDisponible(true);
                domiciliarioRepository.save(pedido.getDomiciliario());
            }
        }

        Pedido guardado = pedidoRepository.save(pedido);
        calcularTotales(guardado);
        return guardado;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Pedido pedido = searchById(id);
        pedidoRepository.delete(pedido);
    }

    @Override
    public Double calcularSubtotal(ItemPedido item) {
        if (item == null)
            return 0.0;

        double precioComida = (item.getComida() != null && item.getComida().getPrecio() != null)
                ? item.getComida().getPrecio()
                : 0.0;
        int cantProducto = (item.getCantidad() != null && item.getCantidad() > 0) ? item.getCantidad() : 1;
        double subComida = precioComida * cantProducto;

        double subAdicionales = 0.0;
        int cantAdicionales = 0;

        if (item.getAdicionales() != null) {
            for (ItemPedidoAdicional ipa : item.getAdicionales()) {
                if (ipa != null && ipa.getAdicional() != null && ipa.getAdicional().getPrecio() != null) {
                    int cantAdic = (ipa.getCantidad() != null && ipa.getCantidad() > 0) ? ipa.getCantidad() : 1;
                    double subAdic = ipa.getAdicional().getPrecio() * cantAdic;
                    ipa.setSubtotal(subAdic);
                    subAdicionales += subAdic;
                    cantAdicionales += cantAdic;
                }
            }
        }

        double subtotalItem = subComida + subAdicionales;
        item.setCantidadAdicionales(cantAdicionales);
        item.setSubtotalComida(subComida);
        item.setSubtotalAdicionales(subAdicionales);
        item.setSubtotal(subtotalItem);

        return subtotalItem;
    }

    @Override
    public Double calcularTotal(Pedido pedido) {
        if (pedido == null || pedido.getItems() == null)
            return 0.0;
        double total = 0.0;
        for (ItemPedido item : pedido.getItems()) {
            total += calcularSubtotal(item);
        }
        return total;
    }

    @Override
    public void calcularTotales(Pedido pedido) {
        if (pedido == null)
            return;

        int totalProductos = 0;
        int totalAdicionales = 0;
        double subComida = 0.0;
        double subAdicionales = 0.0;
        double total = 0.0;

        if (pedido.getItems() != null) {
            for (ItemPedido item : pedido.getItems()) {
                double subItem = calcularSubtotal(item);
                totalProductos += (item.getCantidad() != null) ? item.getCantidad() : 0;
                totalAdicionales += (item.getCantidadAdicionales() != null) ? item.getCantidadAdicionales() : 0;
                subComida += (item.getSubtotalComida() != null) ? item.getSubtotalComida() : 0.0;
                subAdicionales += (item.getSubtotalAdicionales() != null) ? item.getSubtotalAdicionales() : 0.0;
                total += subItem;
            }
        }

        pedido.setCantidadTotalProductos(totalProductos);
        pedido.setCantidadTotalAdicionales(totalAdicionales);
        pedido.setSubtotalComida(subComida);
        pedido.setSubtotalAdicionales(subAdicionales);
        pedido.setTotal(total);
    }
}
