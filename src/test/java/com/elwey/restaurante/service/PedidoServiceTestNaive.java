package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.entities.*;
import com.elwey.restaurante.errors.PedidoNotFoundException;
import com.elwey.restaurante.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Pruebas de integración del servicio de Pedido (Semana 13).
 * Prueba "Naive" real que interactúa con la base de datos de testeo SIN MOCKS.
 * Utiliza @DirtiesContext para aislar cada prueba levantando el contexto de Spring de forma limpia.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PedidoServiceTestNaive {

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

    @Before
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
    public void pedidoService_crearPedido_calculaSubtotalesYPersiste() {
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

        // Aserciones sobre totales calculados por la lógica de negocio
        Assertions.assertThat(pedidoCreado.getSubtotalComida()).isEqualTo(50000.0);
        Assertions.assertThat(pedidoCreado.getSubtotalAdicionales()).isEqualTo(5000.0);
        Assertions.assertThat(pedidoCreado.getCantidadTotalProductos()).isEqualTo(2);
        Assertions.assertThat(pedidoCreado.getCantidadTotalAdicionales()).isEqualTo(1);
        Assertions.assertThat(pedidoCreado.getTotal()).isEqualTo(55000.0);
    }

    /**
     * Prueba 2: Consultar un pedido previamente persistido por su ID.
     * Valida la recuperación exitosa y el lanzamiento de excepción si el pedido no existe.
     */
    @Test
    public void pedidoService_searchById_returnsPedido() {
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

        // Act & Assert 1: Búsqueda exitosa
        Pedido encontrado = pedidoService.searchById(nuevo.getId());
        Assertions.assertThat(encontrado).isNotNull();
        Assertions.assertThat(encontrado.getId()).isEqualTo(nuevo.getId());
        Assertions.assertThat(encontrado.getEstado()).isEqualTo("Cocinando");
        Assertions.assertThat(encontrado.getTotal()).isEqualTo(25000.0);

        // Act & Assert 2: Excepción ante ID inexistente
        Assertions.assertThatThrownBy(() -> pedidoService.searchById(99999L))
                .isInstanceOf(PedidoNotFoundException.class);
    }

    /**
     * Prueba 3: Actualizar el estado de un pedido a 'Enviado' y asignar un domiciliario.
     * Valida la transición operativa y la persistencia de la asignación.
     */
    @Test
    public void pedidoService_actualizarEstado_cambiaEstadoAEnviadoYAsignaDomiciliario() {
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
        Assertions.assertThat(despachado.getDomiciliario().getId()).isEqualTo(domiciliario.getId());
        Assertions.assertThat(despachado.getDomiciliario().getNombre()).isEqualTo("Andrés Parra");
    }

    /**
     * Prueba 4: Actualizar el estado de un pedido a 'Entregado' fija automáticamente la fecha de entrega.
     * Valida la regla de negocio temporal del ciclo de vida del pedido.
     */
    @Test
    public void pedidoService_actualizarEstado_entregadoFijaFechaEntrega() {
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
        Assertions.assertThat(creado.getFechaEntrega()).isNull();

        // Act
        Pedido entregado = pedidoService.actualizarEstado(creado.getId(), "Entregado", null);

        // Assert
        Assertions.assertThat(entregado).isNotNull();
        Assertions.assertThat(entregado.getEstado()).isEqualTo("Entregado");
        Assertions.assertThat(entregado.getFechaEntrega()).isNotNull();
        Assertions.assertThat(entregado.getFechaEntrega()).isAfterOrEqualTo(creado.getFechaCreacion());
    }

    /**
     * Prueba 5: Consultar la lista de pedidos asociados a un cliente en la base de datos real.
     * Valida el aislamiento estricto entre clientes sin fuga de pedidos ajenos.
     */
    @Test
    public void pedidoService_findByCliente_returnsPedidos() {
        // Arrange: Crear 2 pedidos para Falcao
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

        // Crear 1 pedido para un segundo cliente (James)
        Cliente otroCliente = clienteRepository.save(
                Cliente.builder()
                        .nombre("James")
                        .apellido("Rodríguez")
                        .email("james@elwey.com")
                        .password("diez123")
                        .telefono("3115556677")
                        .direccion("Calle 10 #5-20")
                        .activo(true)
                        .build()
        );
        CrearPedidoDto pedidoJames = CrearPedidoDto.builder()
                .clienteId(otroCliente.getId())
                .items(List.of(
                        CrearPedidoDto.ItemPedidoRequestDto.builder()
                                .comidaId(comidaTaco.getId())
                                .cantidad(1)
                                .build()
                ))
                .build();
        pedidoService.crearPedido(pedidoJames);

        // Act
        List<Pedido> pedidosFalcao = pedidoService.findByCliente(cliente.getId());
        List<Pedido> pedidosJames = pedidoService.findByCliente(otroCliente.getId());

        // Assert: Falcao tiene exactamente 2 pedidos y James tiene 1
        Assertions.assertThat(pedidosFalcao).hasSize(2);
        Assertions.assertThat(pedidosFalcao).allMatch(p -> p.getCliente().getId().equals(cliente.getId()));
        Assertions.assertThat(pedidosJames).hasSize(1);
        Assertions.assertThat(pedidosJames.get(0).getCliente().getId()).isEqualTo(otroCliente.getId());
    }
}
