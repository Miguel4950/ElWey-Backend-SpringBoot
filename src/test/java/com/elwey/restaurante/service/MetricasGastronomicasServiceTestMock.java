package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.MetricasGastronomicasDto;
import com.elwey.restaurante.dto.PlatoPopularDto;
import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.entities.ItemPedido;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.repository.ComidaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pruebas unitarias de Servicio con Mocks para Métricas Gastronómicas.
 * Sigue la doctrina estricta del profesor Juan Sebastián Angarita:
 * - @ExtendWith(MockitoExtension.class)
 * - @ActiveProfiles("test")
 * - Nomenclatura de cátedra: [componente]_[metodo]_[lo_que_retorna]
 * - Patrón AAA (Arrange, Act, Assert)
 * - Aislamiento completo de repositorios y dependencias.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class MetricasGastronomicasServiceTestMock {

    @Mock
    private ComidaRepository comidaRepository;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private MetricasGastronomicasServiceImpl metricasService;

    @Test
    @DisplayName("MetricasService - obtenerResumenMetricas - Retorna KPIs calculados correctamente cuando existen pedidos")
    void metricasService_obtenerResumenMetricas_retornaMetricasConPlatosYPedidos() {
        // Arrange
        Categoria catTacos = Categoria.builder().id(1L).nombre("Tacos").build();
        Comida tacoPastor = Comida.builder().id(1L).nombre("Tacos al Pastor").categoria(catTacos).activo(true).precio(22000.0).build();
        Comida tacoBirria = Comida.builder().id(2L).nombre("Tacos de Birria").categoria(catTacos).activo(true).precio(25000.0).build();

        Mockito.when(comidaRepository.findByActivoTrue()).thenReturn(List.of(tacoPastor, tacoBirria));

        Pedido pedido1 = Pedido.builder().id(10L).estado("Entregado").total(44000.0).build();
        ItemPedido item1 = ItemPedido.builder().id(101L).comida(tacoPastor).cantidad(2).pedido(pedido1).build();
        pedido1.setItems(new ArrayList<>(List.of(item1)));

        Pedido pedido2 = Pedido.builder().id(11L).estado("Enviado").total(50000.0).build();
        ItemPedido item2 = ItemPedido.builder().id(102L).comida(tacoBirria).cantidad(2).pedido(pedido2).build();
        pedido2.setItems(new ArrayList<>(List.of(item2)));

        List<Pedido> pedidosSimulados = List.of(pedido1, pedido2);
        Mockito.when(pedidoService.searchAll()).thenReturn(pedidosSimulados);

        // Act
        MetricasGastronomicasDto resultado = metricasService.obtenerResumenMetricas();

        // Assert
        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.getTotalPlatosActivos()).isEqualTo(2L);
        Assertions.assertThat(resultado.getTotalPedidosCompletados()).isEqualTo(2L);
        Assertions.assertThat(resultado.getTicketPromedio()).isEqualTo(47000.0);
        Assertions.assertThat(resultado.getPlatoEstrella()).isNotEmpty();
        Assertions.assertThat(resultado.getIndiceSatisfaccionCliente()).isEqualTo(4.92);
        Assertions.assertThat(resultado.getTiempoEstimadoCocinaMinutos()).isEqualTo(18);
    }

    @Test
    @DisplayName("MetricasService - obtenerResumenMetricas - Retorna valores seguros por defecto cuando no hay pedidos")
    void metricasService_obtenerResumenMetricas_conCeroPedidos_retornaValoresPorDefectoSeguros() {
        // Arrange
        Mockito.when(comidaRepository.findByActivoTrue()).thenReturn(Collections.emptyList());
        Mockito.when(pedidoService.searchAll()).thenReturn(Collections.emptyList());

        // Act
        MetricasGastronomicasDto resultado = metricasService.obtenerResumenMetricas();

        // Assert
        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.getTotalPlatosActivos()).isEqualTo(0L);
        Assertions.assertThat(resultado.getTotalPedidosCompletados()).isEqualTo(0L);
        Assertions.assertThat(resultado.getTicketPromedio()).isEqualTo(38500.0);
        Assertions.assertThat(resultado.getPlatoEstrella()).isEqualTo("Tacos al Pastor con Piña Asada");
    }

    @Test
    @DisplayName("MetricasService - obtenerPlatosMasPopulares - Retorna ranking ordenado descendente por cantidad vendida")
    void metricasService_obtenerPlatosMasPopulares_retornaRankingOrdenado() {
        // Arrange
        Categoria cat = Categoria.builder().id(1L).nombre("Tacos").build();
        Comida tacoPastor = Comida.builder().id(1L).nombre("Tacos al Pastor").categoria(cat).build();
        Comida tacoCarnitas = Comida.builder().id(2L).nombre("Tacos de Carnitas").categoria(cat).build();

        Pedido p1 = Pedido.builder().id(1L).estado("Entregado").build();
        ItemPedido item1 = ItemPedido.builder().id(1L).comida(tacoPastor).cantidad(5).pedido(p1).build();
        ItemPedido item2 = ItemPedido.builder().id(2L).comida(tacoCarnitas).cantidad(2).pedido(p1).build();
        p1.setItems(new ArrayList<>(List.of(item1, item2)));

        Mockito.when(pedidoService.searchAll()).thenReturn(List.of(p1));

        // Act
        List<PlatoPopularDto> ranking = metricasService.obtenerPlatosMasPopulares();

        // Assert
        Assertions.assertThat(ranking).isNotEmpty();
        Assertions.assertThat(ranking.get(0).getNombre()).isEqualTo("Tacos al Pastor");
        Assertions.assertThat(ranking.get(0).getCantidadVendida()).isEqualTo(5);
        Assertions.assertThat(ranking.get(1).getNombre()).isEqualTo("Tacos de Carnitas");
        Assertions.assertThat(ranking.get(1).getCantidadVendida()).isEqualTo(2);
    }
}
