package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.entities.*;
import com.elwey.restaurante.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Pruebas de integración del servicio de Pedido (Semana 13).
 * Pruebas reales que interactúan con la base de datos H2 en memoria SIN MOCKS.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PedidoServiceIntegrationTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    private Cliente cliente;
    private Comida comidaTaco;
    private Adicional adicionalGuacamole;
    private Domiciliario domiciliario;

    @BeforeEach
    public void setUp() {
        Categoria categoria = categoriaRepository.save(
                Categoria.builder()
                        .nombre("Tacos")
                        .descripcion("Tacos mexicanos")
                        .activo(true)
                        .build()
        );

        cliente = clienteRepository.save(
                Cliente.builder()
                        .nombre("Falcao")
                        .apellido("García")
                        .email("falcao@elwey.com")
                        .password("tigre123")
                        .telefono("3109876543")
                        .direccion("Carrera 7 #72-10")
                        .activo(true)
                        .build()
        );

        comidaTaco = comidaRepository.save(
                Comida.builder()
                        .nombre("Tacos de Birria")
                        .precio(25000.0)
                        .descripcion("Tres tacos con consomé")
                        .categoria(categoria)
                        .activo(true)
                        .build()
        );

        adicionalGuacamole = adicionalRepository.save(
                Adicional.builder()
                        .nombre("Guacamole Extra")
                        .precio(5000.0)
                        .activo(true)
                        .build()
        );

        domiciliario = domiciliarioRepository.save(
                Domiciliario.builder()
                        .nombre("Andrés Parra")
                        .cedula("1020304050")
                        .celular("+57 312 444 5555")
                        .contrasena("domi123")
                        .disponible(true)
                        .build()
        );
    }

    /**
     * Prueba 1: Crear pedido con ítems y adicionales de forma real en base de datos.
     * Valida persistencia, cálculo de subtotales, total y estado inicial 'Cocinando'.
     */
    @Test
    public void crearPedido_calculaSubtotalesYPersisteEnBaseDeDatos() {
        // Arrange: 2 tacos (2 * 25000 = 50000) + 1 guacamole (1 * 5000 = 5000) = 55000 total
        CrearPedidoDto.AdicionalCantidadDto adicDto = CrearPedidoDto.AdicionalCantidadDto.builder()
                .adicionalId(adicionalGuacamole.getId())
                .cantidad(1)
                .build();

        CrearPedidoDto.ItemPedidoRequestDto itemDto = CrearPedidoDto.ItemPedidoRequestDto.builder()
                .comidaId(comidaTaco.getId())
                .cantidad(2)
                .adicionales(List.of(adicDto))
                .build();

        CrearPedidoDto pedidoRequest = CrearPedidoDto.builder()
                .clienteId(cliente.getId())
                .items(List.of(itemDto))
                .build();

        // Act
        Pedido pedidoCreado = pedidoService.crearPedido(pedidoRequest);

        // Assert
        Assertions.assertThat(pedidoCreado).isNotNull();
        Assertions.assertThat(pedidoCreado.getId()).isNotNull();
        Assertions.assertThat(pedidoCreado.getEstado()).isEqualTo("Cocinando");
        Assertions.assertThat(pedidoCreado.getCliente().getId()).isEqualTo(cliente.getId());
        Assertions.assertThat(pedidoCreado.getItems()).hasSize(1);
        Assertions.assertThat(pedidoCreado.getTotal()).isEqualTo(55000.0);
    }

    /**
     * Prueba 2: Consultar un pedido previamente persistido por su ID.
     */
    @Test
    public void searchById_retornaPedidoExistente() {
        // Arrange
        CrearPedidoDto pedidoRequest = CrearPedidoDto.builder()
                .clienteId(cliente.getId())
                .items(List.of(
                        CrearPedidoDto.ItemPedidoRequestDto.builder()
                                .comidaId(comidaTaco.getId())
                                .cantidad(1)
                                .adicionales(new ArrayList<>())
                                .build()
                ))
                .build();
        Pedido nuevo = pedidoService.crearPedido(pedidoRequest);

        // Act
        Pedido encontrado = pedidoService.searchById(nuevo.getId());

        // Assert
        Assertions.assertThat(encontrado).isNotNull();
        Assertions.assertThat(encontrado.getId()).isEqualTo(nuevo.getId());
        Assertions.assertThat(encontrado.getEstado()).isEqualTo("Cocinando");
    }

    /**
     * Prueba 3: Actualizar el estado de un pedido a 'Enviado' y asignar un domiciliario.
     */
    @Test
    public void actualizarEstado_cambiaEstadoAEnviadoYAsignaDomiciliario() {
        // Arrange
        CrearPedidoDto pedidoRequest = CrearPedidoDto.builder()
                .clienteId(cliente.getId())
                .items(List.of(
                        CrearPedidoDto.ItemPedidoRequestDto.builder()
                                .comidaId(comidaTaco.getId())
                                .cantidad(1)
                                .adicionales(new ArrayList<>())
                                .build()
                ))
                .build();
        Pedido creado = pedidoService.crearPedido(pedidoRequest);

        // Act
        Pedido despachado = pedidoService.actualizarEstado(creado.getId(), "Enviado", domiciliario.getId());

        // Assert
        Assertions.assertThat(despachado).isNotNull();
        Assertions.assertThat(despachado.getEstado()).isEqualTo("Enviado");
        Assertions.assertThat(despachado.getDomiciliario()).isNotNull();
        Assertions.assertThat(despachado.getDomiciliario().getNombre()).isEqualTo("Andrés Parra");
    }

    /**
     * Prueba 4: Actualizar el estado de un pedido a 'Entregado' fija automáticamente la fecha de entrega.
     */
    @Test
    public void actualizarEstado_entregadoFijaFechaEntrega() {
        // Arrange
        CrearPedidoDto pedidoRequest = CrearPedidoDto.builder()
                .clienteId(cliente.getId())
                .items(List.of(
                        CrearPedidoDto.ItemPedidoRequestDto.builder()
                                .comidaId(comidaTaco.getId())
                                .cantidad(1)
                                .adicionales(new ArrayList<>())
                                .build()
                ))
                .build();
        Pedido creado = pedidoService.crearPedido(pedidoRequest);

        // Act
        Pedido entregado = pedidoService.actualizarEstado(creado.getId(), "Entregado", null);

        // Assert
        Assertions.assertThat(entregado).isNotNull();
        Assertions.assertThat(entregado.getEstado()).isEqualTo("Entregado");
        Assertions.assertThat(entregado.getFechaEntrega()).isNotNull();
    }

    /**
     * Prueba 5: Consultar la lista de pedidos asociados a un cliente en la base de datos real.
     */
    @Test
    public void findByCliente_retornaPedidosAsociadosAlCliente() {
        // Arrange: Crear 2 pedidos para el cliente Falcao
        CrearPedidoDto pedido1 = CrearPedidoDto.builder()
                .clienteId(cliente.getId())
                .items(List.of(
                        CrearPedidoDto.ItemPedidoRequestDto.builder()
                                .comidaId(comidaTaco.getId())
                                .cantidad(1)
                                .build()
                ))
                .build();
        CrearPedidoDto pedido2 = CrearPedidoDto.builder()
                .clienteId(cliente.getId())
                .items(List.of(
                        CrearPedidoDto.ItemPedidoRequestDto.builder()
                                .comidaId(comidaTaco.getId())
                                .cantidad(2)
                                .build()
                ))
                .build();

        pedidoService.crearPedido(pedido1);
        pedidoService.crearPedido(pedido2);

        // Act
        List<Pedido> pedidosCliente = pedidoService.findByCliente(cliente.getId());

        // Assert
        Assertions.assertThat(pedidosCliente).isNotNull();
        Assertions.assertThat(pedidosCliente.size()).isEqualTo(2);
    }
}
