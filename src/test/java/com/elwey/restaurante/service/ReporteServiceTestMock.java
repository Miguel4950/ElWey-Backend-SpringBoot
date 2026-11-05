package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.Pedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReporteServiceTestMock {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    @Test
    @DisplayName("reporteService_generarReportePedidosCsv_retornaCsvConCabecerasYDatos")
    void reporteService_generarReportePedidosCsv_retornaCsvConCabecerasYDatos() {
        // Arrange
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Miguel Angel")
                .email("miguel@elwey.com")
                .telefono("3001234567")
                .direccion("Calle 72 # 11-86")
                .build();

        Domiciliario domiciliario = Domiciliario.builder()
                .id(2L)
                .cedula("10102020")
                .nombre("Carlos Mendoza")
                .celular("3109876543")
                .disponible(true)
                .build();

        Pedido pedido = Pedido.builder()
                .id(100L)
                .estado("ENTREGADO")
                .fechaCreacion(LocalDateTime.of(2026, 11, 5, 14, 30, 0))
                .cliente(cliente)
                .domiciliario(domiciliario)
                .cantidadTotalProductos(3)
                .cantidadTotalAdicionales(2)
                .subtotalComida(35000.0)
                .subtotalAdicionales(5000.0)
                .total(40000.0)
                .build();

        when(pedidoService.searchAll()).thenReturn(List.of(pedido));

        // Act
        byte[] resultado = reporteService.generarReportePedidosCsv();
        String csvContenido = new String(resultado, StandardCharsets.UTF_8);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isNotEmpty();
        assertThat(csvContenido).startsWith("\uFEFF"); // Verifica BOM UTF-8
        assertThat(csvContenido).contains("ID Pedido;Fecha Creacion;Estado;Cliente;Telefono Cliente;Direccion Entrega");
        assertThat(csvContenido).contains("100;2026-11-05 14:30:00;ENTREGADO;Miguel Angel;3001234567;Calle 72 # 11-86;3;2;35000;5000;40000;Carlos Mendoza");

        verify(pedidoService, times(1)).searchAll();
        verify(pedidoService, times(1)).calcularTotales(pedido);
    }

    @Test
    @DisplayName("reporteService_generarReportePedidosCsv_listaVacia_retornaSoloCabecerasConBom")
    void reporteService_generarReportePedidosCsv_listaVacia_retornaSoloCabecerasConBom() {
        // Arrange
        when(pedidoService.searchAll()).thenReturn(Collections.emptyList());

        // Act
        byte[] resultado = reporteService.generarReportePedidosCsv();
        String csvContenido = new String(resultado, StandardCharsets.UTF_8);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(csvContenido).startsWith("\uFEFF");
        assertThat(csvContenido).contains("ID Pedido;Fecha Creacion;Estado;Cliente;Telefono Cliente;Direccion Entrega");

        // Solo debe contener la línea de cabecera tras el BOM
        String[] lineas = csvContenido.replace("\uFEFF", "").split("\n");
        assertThat(lineas).hasSize(1);

        verify(pedidoService, times(1)).searchAll();
    }
}
