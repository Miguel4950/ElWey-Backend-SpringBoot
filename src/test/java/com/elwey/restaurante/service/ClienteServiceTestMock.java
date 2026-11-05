package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.ClienteResumenDto;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.errors.ClienteNotFoundException;
import com.elwey.restaurante.repository.ClienteRepository;
import com.elwey.restaurante.repository.PedidoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Pruebas unitarias de Servicio con Mocks para ClienteService.
 * Sigue la doctrina del profesor Angarita:
 * - @ExtendWith(MockitoExtension.class)
 * - @ActiveProfiles("test")
 * - Patrón Arrange - Act - Assert con AssertJ
 * - Nomenclatura [componente]_[metodo]_[lo_que_retorna]
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ClienteServiceTestMock {

    @Mock
    private ClienteRepository clienteRepo;

    @Mock
    private PedidoRepository pedidoRepo;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    /**
     * Prueba [RESUMEN - Exito]: Calcular métricas acumuladas de compras de un cliente con pedidos.
     * Retorna ClienteResumenDto con total pedidos y total gastado correctos.
     */
    @Test
    public void clienteService_obtenerResumenCliente_retornaMetricasYTotalGastado() {
        // Arrange
        Long clienteId = 1L;
        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@elwey.com")
                .telefono("+57 300 111 2233")
                .direccion("Cra 7 # 45-20")
                .activo(true)
                .build();

        Pedido p1 = Pedido.builder()
                .id(101L)
                .estado("Entregado")
                .total(45000.0)
                .fechaCreacion(LocalDateTime.of(2026, 11, 1, 12, 30))
                .build();

        Pedido p2 = Pedido.builder()
                .id(102L)
                .estado("Entregado")
                .total(35000.0)
                .fechaCreacion(LocalDateTime.of(2026, 11, 3, 19, 45))
                .build();

        Mockito.when(clienteRepo.findById(clienteId)).thenReturn(Optional.of(cliente));
        Mockito.when(pedidoService.findByCliente(clienteId)).thenReturn(List.of(p1, p2));

        // Act
        ClienteResumenDto resumen = clienteService.obtenerResumenCliente(clienteId);

        // Assert
        Assertions.assertThat(resumen).isNotNull();
        Assertions.assertThat(resumen.getClienteId()).isEqualTo(clienteId);
        Assertions.assertThat(resumen.getEmail()).isEqualTo("juan.perez@elwey.com");
        Assertions.assertThat(resumen.getTotalPedidos()).isEqualTo(2);
        Assertions.assertThat(resumen.getTotalGastado()).isEqualTo(80000.0);
        Assertions.assertThat(resumen.getUltimoPedidoFecha()).isEqualTo("03/11/2026 19:45");
        Mockito.verify(clienteRepo).findById(clienteId);
        Mockito.verify(pedidoService).findByCliente(clienteId);
    }

    /**
     * Prueba [RESUMEN - Cliente sin pedidos]: Cliente nuevo sin historial de compras.
     * Retorna ClienteResumenDto con 0 pedidos y 0.0 gastado.
     */
    @Test
    public void clienteService_obtenerResumenCliente_clienteSinPedidos_retornaValoresEnCero() {
        // Arrange
        Long clienteId = 2L;
        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .nombre("María")
                .apellido("González")
                .email("maria@elwey.com")
                .activo(true)
                .build();

        Mockito.when(clienteRepo.findById(clienteId)).thenReturn(Optional.of(cliente));
        Mockito.when(pedidoService.findByCliente(clienteId)).thenReturn(List.of());

        // Act
        ClienteResumenDto resumen = clienteService.obtenerResumenCliente(clienteId);

        // Assert
        Assertions.assertThat(resumen).isNotNull();
        Assertions.assertThat(resumen.getTotalPedidos()).isEqualTo(0);
        Assertions.assertThat(resumen.getTotalGastado()).isEqualTo(0.0);
        Assertions.assertThat(resumen.getUltimoPedidoFecha()).isEqualTo("Sin pedidos registrados");
    }

    /**
     * Prueba [RESUMEN - Error NotFound]: Intentar consultar resumen de cliente inexistente.
     * Lanza ClienteNotFoundException.
     */
    @Test
    public void clienteService_obtenerResumenCliente_clienteInexistente_lanzaExcepcion() {
        // Arrange
        Long clienteId = 999L;
        Mockito.when(clienteRepo.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> clienteService.obtenerResumenCliente(clienteId))
                .isInstanceOf(ClienteNotFoundException.class);
        Mockito.verify(clienteRepo).findById(clienteId);
        Mockito.verifyNoInteractions(pedidoService);
    }
}
