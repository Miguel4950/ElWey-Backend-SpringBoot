package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.ItemPedido;
import com.elwey.restaurante.entities.ItemPedidoAdicional;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.errors.ComidaNotFoundException;
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
    @Transactional
    public void save(Pedido pedido) {
        pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Pedido pedido = searchById(id);
        if (pedido.getItems() != null) {
            for (ItemPedido item : pedido.getItems()) {
                if (item.getAdicionales() != null) {
                    item.getAdicionales().clear();
                }
            }
            pedido.getItems().clear();
        }
        pedidoRepository.delete(pedido);
    }

    @Override
    @Transactional
    public void crearPedidoRapido(Long comidaId, List<Long> adicionalesIds) {
        List<CrearPedidoDto.AdicionalCantidadDto> adics = new ArrayList<>();
        if (adicionalesIds != null) {
            for (Long id : adicionalesIds) {
                if (id != null) {
                    adics.add(CrearPedidoDto.AdicionalCantidadDto.builder()
                            .adicionalId(id)
                            .cantidad(1)
                            .build());
                }
            }
        }
        crearPedido(CrearPedidoDto.builder()
                .comidaId(comidaId)
                .cantidad(1)
                .adicionales(adics)
                .build());
    }

    @Override
    @Transactional
    public Pedido crearPedido(CrearPedidoDto dto) {
        Cliente cliente = null;
        if (dto.getClienteId() != null) {
            cliente = clienteRepository.findById(dto.getClienteId()).orElse(null);
        }
        if (cliente == null) {
            List<Cliente> clientes = clienteRepository.findAll();
            cliente = clientes.isEmpty() ? null : clientes.get(0);
        }

        List<Domiciliario> domiciliarios = domiciliarioRepository.findAll();
        Domiciliario domiciliario = domiciliarios.isEmpty() ? null : domiciliarios.get(0);

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .domiciliario(domiciliario)
                .estado("En preparación")
                .fechaCreacion(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        pedido = pedidoRepository.save(pedido);

        // Caso 1: Pedido desde el Carrito de Compras (múltiples ítems/comidas)
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (CrearPedidoDto.ItemPedidoRequestDto itemReq : dto.getItems()) {
                if (itemReq != null && itemReq.getComidaId() != null) {
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
                            if (adicReq != null && adicReq.getAdicionalId() != null
                                    && adicReq.getCantidad() != null && adicReq.getCantidad() > 0) {
                                Adicional adicional = adicionalRepository.findById(adicReq.getAdicionalId()).orElse(null);
                                if (adicional != null) {
                                    ItemPedidoAdicional relacion = ItemPedidoAdicional.builder()
                                            .itemPedido(item)
                                            .adicional(adicional)
                                            .cantidad(adicReq.getCantidad())
                                            .build();
                                    relacion = itemPedidoAdicionalRepository.save(relacion);
                                    item.getAdicionales().add(relacion);
                                }
                            }
                        }
                    }
                    pedido.getItems().add(item);
                }
            }
        }
        // Caso 2: Pedido individual directo (retrocompatibilidad)
        else if (dto.getComidaId() != null) {
            Comida comida = comidaRepository.findById(dto.getComidaId())
                    .orElseThrow(() -> new ComidaNotFoundException(dto.getComidaId()));
            int cantidadFinal = (dto.getCantidad() != null && dto.getCantidad() > 0) ? dto.getCantidad() : 1;

            ItemPedido item = ItemPedido.builder()
                    .pedido(pedido)
                    .comida(comida)
                    .cantidad(cantidadFinal)
                    .adicionales(new ArrayList<>())
                    .build();
            item = itemPedidoRepository.save(item);

            if (dto.getAdicionales() != null) {
                for (CrearPedidoDto.AdicionalCantidadDto adicReq : dto.getAdicionales()) {
                    if (adicReq != null && adicReq.getAdicionalId() != null
                            && adicReq.getCantidad() != null && adicReq.getCantidad() > 0) {
                        Adicional adicional = adicionalRepository.findById(adicReq.getAdicionalId()).orElse(null);
                        if (adicional != null) {
                            ItemPedidoAdicional relacion = ItemPedidoAdicional.builder()
                                    .itemPedido(item)
                                    .adicional(adicional)
                                    .cantidad(adicReq.getCantidad())
                                    .build();
                            relacion = itemPedidoAdicionalRepository.save(relacion);
                            item.getAdicionales().add(relacion);
                        }
                    }
                }
            }
            pedido.getItems().add(item);
        }

        calcularTotales(pedido);
        return pedido;
    }

    @Override
    public Double calcularSubtotal(ItemPedido item) {
        if (item == null) return 0.0;
        double precioComida = (item.getComida() != null && item.getComida().getPrecio() != null)
                ? item.getComida().getPrecio() : 0.0;
        double sumaAdicionales = 0.0;
        if (item.getAdicionales() != null) {
            for (ItemPedidoAdicional ipa : item.getAdicionales()) {
                if (ipa != null && ipa.getAdicional() != null && ipa.getAdicional().getPrecio() != null) {
                    int cantAdic = (ipa.getCantidad() != null && ipa.getCantidad() > 0) ? ipa.getCantidad() : 1;
                    sumaAdicionales += ipa.getAdicional().getPrecio() * cantAdic;
                }
            }
        }
        int cantidad = (item.getCantidad() != null && item.getCantidad() > 0) ? item.getCantidad() : 1;
        return (precioComida + sumaAdicionales) * cantidad;
    }

    @Override
    public Double calcularTotal(Pedido pedido) {
        if (pedido == null || pedido.getItems() == null) return 0.0;
        double total = 0.0;
        for (ItemPedido item : pedido.getItems()) {
            total += calcularSubtotal(item);
        }
        return total;
    }

    @Override
    public void calcularTotales(Pedido pedido) {
        if (pedido == null) return;
        double total = 0.0;
        if (pedido.getItems() != null) {
            for (ItemPedido item : pedido.getItems()) {
                double sub = calcularSubtotal(item);
                item.setSubtotal(sub);
                total += sub;
            }
        }
        pedido.setTotal(total);
    }
}
