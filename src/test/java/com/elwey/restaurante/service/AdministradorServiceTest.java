package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.ResumenEstadisticasDto;
import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.repository.AdministradorRepository;
import com.elwey.restaurante.repository.ComidaRepository;
import com.elwey.restaurante.repository.DomiciliarioRepository;
import com.elwey.restaurante.repository.PedidoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AdministradorServiceTest {

    @Mock
    private AdministradorRepository administradorRepo;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ComidaRepository comidaRepository;

    @Mock
    private DomiciliarioRepository domiciliarioRepository;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private AdministradorServiceImpl administradorService;

    @Test
    @DisplayName("obtenerResumenEstadisticas retorna métricas consolidadas exitosamente")
    public void administradorService_obtenerResumenEstadisticas_retornaMetricasConsolidadas() {
        // Arrange
        Mockito.when(pedidoRepository.count()).thenReturn(15L);
        Mockito.when(pedidoRepository.countPedidosPorEstado("Cocinando")).thenReturn(3L);
        Mockito.when(pedidoRepository.countPedidosPorEstado("Listo")).thenReturn(2L);
        Mockito.when(pedidoRepository.countPedidosPorEstado("Enviado")).thenReturn(4L);
        Mockito.when(pedidoRepository.countPedidosPorEstado("Entregado")).thenReturn(6L);

        Pedido pedidoEntregado = Pedido.builder().id(1L).estado("Entregado").total(58000.0).build();
        Mockito.when(pedidoRepository.findByEstado("Entregado")).thenReturn(List.of(pedidoEntregado));
        Mockito.doAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setTotal(58000.0);
            return null;
        }).when(pedidoService).calcularTotales(any(Pedido.class));

        Mockito.when(comidaRepository.findByActivoTrue()).thenReturn(List.of(
                Comida.builder().id(1L).nombre("Taco").activo(true).build(),
                Comida.builder().id(2L).nombre("Burrito").activo(true).build()
        ));

        Mockito.when(domiciliarioRepository.findByDisponible(true)).thenReturn(List.of(
                Domiciliario.builder().id(1L).nombre("Carlos").disponible(true).build()
        ));
        Mockito.when(domiciliarioRepository.count()).thenReturn(3L);

        // Act
        ResumenEstadisticasDto resumen = administradorService.obtenerResumenEstadisticas();

        // Assert
        assertThat(resumen).isNotNull();
        assertThat(resumen.getTotalPedidos()).isEqualTo(15L);
        assertThat(resumen.getPedidosCocinando()).isEqualTo(3L);
        assertThat(resumen.getPedidosListos()).isEqualTo(2L);
        assertThat(resumen.getPedidosEnviados()).isEqualTo(4L);
        assertThat(resumen.getPedidosEntregados()).isEqualTo(6L);
        assertThat(resumen.getTotalVentasEntregadas()).isEqualTo(58000.0);
        assertThat(resumen.getTotalComidasActivas()).isEqualTo(2L);
        assertThat(resumen.getDomiciliariosDisponibles()).isEqualTo(1L);
        assertThat(resumen.getTotalDomiciliarios()).isEqualTo(3L);
    }
}
