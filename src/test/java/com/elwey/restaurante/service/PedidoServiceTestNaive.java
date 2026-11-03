package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.entities.*;
import com.elwey.restaurante.errors.PedidoNotFoundException;
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
 * Pruebas unitarias/de integración de Servicio Naive (Semana 13).
 * Valida la lógica de negocio real de PedidoServiceImpl persistiendo directamente en la base de datos H2.
 * Sigue la doctrina del docente Juan Sebastián Angarita:
 * - JUnit 5 Jupiter con @SpringBootTest y @BeforeEach
 * - Patrón Arrange - Act - Assert con AssertJ
 * - Nomenclatura doctrinal: [componente]_[metodo]_[lo_que_retorna]
 * - Separación de escenarios de éxito y fallo en métodos atómicos independientes.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PedidoServiceTestNaive {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    private Cliente cliente;
    private Domiciliario domiciliario;
    private Comida comidaTaco;
    private Adicional adicionalGuacamole;

    @BeforeEach
    public void setUp() {
        Categoria cat = categoriaRepository.save(
                Categoria.builder()
                        .nombre("Tacos")
                        .descripcion("Auténticos tacos mexicanos")
                        .activo(true)
                        .build()
        );

        comidaTaco = comidaRepository.save(
                Comida.builder()
                        .nombre("Taco al Pastor")
                        .precio(25000.0)
                        .descripcion("Pastor tradicional con piña")
                        .activo(true)
                        .categoria(cat)
                        .build()
        );

        adicionalGuacamole = adicionalRepository.save(
                Adicional.builder()
                        .nombre("Porción de Guacamole")
                        .precio(5000.0)
                        .activo(true)
                        .build()
        );

        cliente = clienteRepository.save(
                Cliente.builder()
                        .nombre("Radamel")
                        .apellido("Falcao")
                        .email("falcao@elwey.com")
                        .password("tigre123")
                        .telefono("3001234567")
                        .direccion("Calle 100 #15-20")
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
     * Prueba 1 [CREATE - Caso Positivo]: Crear un pedido completo persistiendo en BD y calculando totales reales.
     * Retorna la entidad Pedido con el desglose contable correcto.
     */
    @Test
    public void pedidoService_crearPedido_Pedido() {
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
     * Prueba 2 [READ BY ID - Caso Positivo]: Consultar un pedido previamente persistido por su ID.
     * Retorna el objeto Pedido existente.
     */
    @Test
    public void pedidoService_searchById_Pedido() {
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
        Assertions.assertThat(encontrado.getTotal()).isEqualTo(25000.0);
    }

    /**
     * Prueba 3 [READ BY ID - Caso Negativo]: Buscar un pedido inexistente.
     * Retorna NotFound / lanza excepción PedidoNotFoundException.
     */
    @Test
    public void pedidoService_searchById_returnsNotFound() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> pedidoService.searchById(99999L))
                .isInstanceOf(PedidoNotFoundException.class);
    }

    /**
     * Prueba 4 [UPDATE - Caso Positivo]: Actualizar el estado de un pedido a 'Enviado' y asignar un domiciliario.
     * Retorna el objeto Pedido actualizado.
     */
    @Test
    public void pedidoService_actualizarEstado_Pedido() {
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
     * Prueba 5 [UPDATE - Caso Negativo]: Intentar actualizar un pedido con ID inexistente.
     * Retorna NotFound / lanza PedidoNotFoundException.
     */
    @Test
    public void pedidoService_actualizarEstado_returnsNotFound() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> pedidoService.actualizarEstado(88888L, "Enviado", domiciliario.getId()))
                .isInstanceOf(PedidoNotFoundException.class);
    }

    /**
     * Prueba 6 [UPDATE Entregado - Caso Positivo]: Actualizar el estado de un pedido a 'Entregado' fija la fecha de entrega.
     * Retorna el objeto Pedido con fecha de entrega completada.
     */
    @Test
    public void pedidoService_actualizarEstadoEntregado_Pedido() {
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
     * Prueba 7 [READ Lista - Caso Positivo]: Consultar la lista de pedidos asociados a un cliente que tiene órdenes.
     * Retorna una lista llena (returnsList) de pedidos correspondientes.
     */
    @Test
    public void pedidoService_findByCliente_returnsList() {
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

        // Act
        List<Pedido> pedidosFalcao = pedidoService.findByCliente(cliente.getId());

        // Assert: Falcao tiene exactamente 2 pedidos
        Assertions.assertThat(pedidosFalcao).isNotNull().hasSize(2);
        Assertions.assertThat(pedidosFalcao).allMatch(p -> p.getCliente().getId().equals(cliente.getId()));
    }

    /**
     * Prueba 8 [READ Lista - Caso Vacío]: Consultar la lista de pedidos de un cliente registrado sin pedidos.
     * Retorna una lista vacía (returnsEmpty) sin errores.
     */
    @Test
    public void pedidoService_findByCliente_returnsEmpty() {
        // Arrange: Crear cliente nuevo sin pedidos
        Cliente nuevoCliente = clienteRepository.save(
                Cliente.builder()
                        .nombre("James")
                        .apellido("Rodríguez")
                        .email("james.sinpedidos@elwey.com")
                        .password("diez123")
                        .telefono("3115556677")
                        .direccion("Calle 10 #5-20")
                        .activo(true)
                        .build()
        );

        // Act
        List<Pedido> pedidos = pedidoService.findByCliente(nuevoCliente.getId());

        // Assert
        Assertions.assertThat(pedidos).isNotNull().isEmpty();
    }
}
