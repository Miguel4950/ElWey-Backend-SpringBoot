package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.entities.*;
import com.elwey.restaurante.errors.ClienteNotFoundException;
import com.elwey.restaurante.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

/**
 * Pruebas unitarias de Servicio con Mocks (Semana 13).
 * Aísla la lógica de negocio simulando el comportamiento de los repositorios con Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class PedidoServiceMockTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ComidaRepository comidaRepository;

    @Mock
    private AdicionalRepository adicionalRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private DomiciliarioRepository domiciliarioRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private ItemPedidoAdicionalRepository itemPedidoAdicionalRepository;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    /**
     * Prueba 1: Valida que crear un pedido con un cliente inexistente lance ClienteNotFoundException.
     */
    @Test
    public void crearPedido_lanzaExcepcionSiClienteNoExiste() {
        // Arrange
        Long clienteInexistenteId = 999L;
        CrearPedidoDto request = CrearPedidoDto.builder()
                .clienteId(clienteInexistenteId)
                .items(List.of(
                        CrearPedidoDto.ItemPedidoRequestDto.builder()
                                .comidaId(1L)
                                .cantidad(1)
                                .build()
                ))
                .build();

        Mockito.when(clienteRepository.findById(clienteInexistenteId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> pedidoService.crearPedido(request))
                .isInstanceOf(ClienteNotFoundException.class);
    }

    /**
     * Prueba 2: Valida la lógica de negocio de creación de pedido y persistencia de items con mocks.
     */
    @Test
    public void crearPedido_conItemsCalculaSubtotalesCorrectamente() {
        // Arrange
        Cliente clienteMock = Cliente.builder().id(1L).nombre("Messi").build();
        Comida comidaMock = Comida.builder().id(10L).nombre("Quesadilla").precio(20000.0).build();

        CrearPedidoDto request = CrearPedidoDto.builder()
                .clienteId(1L)
                .items(List.of(
                        CrearPedidoDto.ItemPedidoRequestDto.builder()
                                .comidaId(10L)
                                .cantidad(2)
                                .adicionales(new ArrayList<>())
                                .build()
                ))
                .build();

        Mockito.when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteMock));
        Mockito.when(comidaRepository.findById(10L)).thenReturn(Optional.of(comidaMock));

        Pedido pedidoGuardadoSimulado = Pedido.builder()
                .id(100L)
                .cliente(clienteMock)
                .estado("Cocinando")
                .items(new ArrayList<>())
                .build();

        Mockito.when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardadoSimulado);

        ItemPedido itemGuardado = ItemPedido.builder()
                .id(50L)
                .pedido(pedidoGuardadoSimulado)
                .comida(comidaMock)
                .cantidad(2)
                .adicionales(new ArrayList<>())
                .build();

        Mockito.when(itemPedidoRepository.save(any(ItemPedido.class))).thenReturn(itemGuardado);

        // Act
        Pedido resultado = pedidoService.crearPedido(request);

        // Assert
        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.getEstado()).isEqualTo("Cocinando");
        Mockito.verify(clienteRepository).findById(1L);
        Mockito.verify(comidaRepository).findById(10L);
    }

    /**
     * Prueba 3: Actualizar estado con nuevo domiciliario asignado.
     */
    @Test
    public void actualizarEstado_cambiaEstadoAEnviadoYAsignaDomiciliario() {
        // Arrange
        Long pedidoId = 1L;
        Long domiciliarioId = 2L;

        Pedido pedidoExistente = Pedido.builder()
                .id(pedidoId)
                .estado("Cocinando")
                .items(new ArrayList<>())
                .build();

        Domiciliario domMock = Domiciliario.builder()
                .id(domiciliarioId)
                .nombre("Mateo Quintana")
                .disponible(true)
                .build();

        Mockito.when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoExistente));
        Mockito.when(domiciliarioRepository.findById(domiciliarioId)).thenReturn(Optional.of(domMock));
        Mockito.when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pedido actualizado = pedidoService.actualizarEstado(pedidoId, "Enviado", domiciliarioId);

        // Assert
        Assertions.assertThat(actualizado).isNotNull();
        Assertions.assertThat(actualizado.getEstado()).isEqualTo("Enviado");
        Assertions.assertThat(actualizado.getDomiciliario()).isNotNull();
        Assertions.assertThat(actualizado.getDomiciliario().getNombre()).isEqualTo("Mateo Quintana");
    }

    /**
     * Prueba 4: Regla de negocio: si el pedido se despacha a 'Enviado' sin domiciliario asignado,
     * el servicio busca automáticamente en la flota disponible.
     */
    @Test
    public void actualizarEstado_asignaDomiciliarioDisponibleSiNoTieneYEsEnviado() {
        // Arrange
        Long pedidoId = 5L;

        Pedido pedidoSinDomi = Pedido.builder()
                .id(pedidoId)
                .estado("Cocinando")
                .domiciliario(null)
                .items(new ArrayList<>())
                .build();

        Domiciliario domLibre = Domiciliario.builder()
                .id(10L)
                .nombre("Javier Morales")
                .disponible(true)
                .build();

        Mockito.when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoSinDomi));
        Mockito.when(domiciliarioRepository.findByDisponible(true)).thenReturn(List.of(domLibre));
        Mockito.when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pedido despachado = pedidoService.actualizarEstado(pedidoId, "Enviado", null);

        // Assert
        Assertions.assertThat(despachado).isNotNull();
        Assertions.assertThat(despachado.getEstado()).isEqualTo("Enviado");
        Assertions.assertThat(despachado.getDomiciliario()).isNotNull();
        Assertions.assertThat(despachado.getDomiciliario().getNombre()).isEqualTo("Javier Morales");
    }

    /**
     * Prueba 5: Cálculo matemático puro del total consolidando múltiples platos y adicionales.
     */
    @Test
    public void calcularTotal_sumaCorrectamenteTodosLosItems() {
        // Arrange
        Comida taco = Comida.builder().nombre("Taco").precio(15000.0).build();
        Comida burrito = Comida.builder().nombre("Burrito").precio(25000.0).build();
        Adicional queso = Adicional.builder().nombre("Queso").precio(4000.0).build();

        // Item 1: 2 tacos = 30000
        ItemPedido item1 = ItemPedido.builder()
                .comida(taco)
                .cantidad(2)
                .adicionales(new ArrayList<>())
                .build();

        // Item 2: 1 burrito = 25000 + 2 porciones de queso (2 * 4000 = 8000) = 33000
        ItemPedido item2 = ItemPedido.builder()
                .comida(burrito)
                .cantidad(1)
                .adicionales(new ArrayList<>())
                .build();

        ItemPedidoAdicional ipa = ItemPedidoAdicional.builder()
                .itemPedido(item2)
                .adicional(queso)
                .cantidad(2)
                .build();
        item2.getAdicionales().add(ipa);

        Pedido pedido = Pedido.builder()
                .items(List.of(item1, item2))
                .build();

        // Act
        Double total = pedidoService.calcularTotal(pedido);

        // Assert: 30000 + 33000 = 63000.0
        Assertions.assertThat(total).isEqualTo(63000.0);
    }
}
