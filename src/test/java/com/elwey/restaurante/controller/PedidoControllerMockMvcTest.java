package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.ActualizarEstadoPedidoDto;
import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de Controlador con MockMvc (Semana 13).
 * Simula peticiones HTTP REST completas sobre PedidoController con MockBean para aislar la capa de servicio.
 * Cubre 5 tipos de consultas/verbos HTTP: GET todos, GET por ID, GET con filtro, POST crear y PUT actualizar.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PedidoControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Consulta 1 [GET]: Listar todos los pedidos registrados en el restaurante.
     * Espera HTTP 200 OK y una lista JSON de longitud 2.
     */
    @Test
    public void pedidoController_mostrarPedidos_retorna200YLista() throws Exception {
        // Arrange
        Cliente cliente = Cliente.builder().id(1L).nombre("Messi").email("messi@elwey.com").build();
        Pedido p1 = Pedido.builder().id(1L).cliente(cliente).estado("Cocinando").fechaCreacion(LocalDateTime.now()).items(new ArrayList<>()).build();
        Pedido p2 = Pedido.builder().id(2L).cliente(cliente).estado("Enviado").fechaCreacion(LocalDateTime.now()).items(new ArrayList<>()).build();

        Mockito.when(pedidoService.searchAll()).thenReturn(List.of(p1, p2));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(2)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].estado", Matchers.is("Cocinando")))
                .andExpect(jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(jsonPath("$[1].estado", Matchers.is("Enviado")));
    }

    /**
     * Consulta 2 [GET con ID]: Obtener el detalle consolidado de un pedido por su identificador.
     * Espera HTTP 200 OK y el objeto JSON con su ID correspondiente.
     */
    @Test
    public void pedidoController_mostrarPedidoPorId_retorna200YPedido() throws Exception {
        // Arrange
        Cliente cliente = Cliente.builder().id(3L).nombre("Falcao").email("falcao@elwey.com").build();
        Pedido pedido = Pedido.builder().id(10L).cliente(cliente).estado("Cocinando").fechaCreacion(LocalDateTime.now()).items(new ArrayList<>()).build();

        Mockito.when(pedidoService.searchById(10L)).thenReturn(pedido);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(10)))
                .andExpect(jsonPath("$.estado", Matchers.is("Cocinando")))
                .andExpect(jsonPath("$.clienteNombre", Matchers.is("Falcao")));
    }

    /**
     * Consulta 3 [GET con Filtro]: Consultar los pedidos asignados a un domiciliario específico.
     * Espera HTTP 200 OK y la lista de pedidos asignados al repartidor.
     */
    @Test
    public void pedidoController_mostrarPedidosPorDomiciliario_retorna200YListaFiltrada() throws Exception {
        // Arrange
        Long domiId = 2L;
        Domiciliario domiciliario = Domiciliario.builder().id(domiId).nombre("Andrés Parra").build();
        Cliente cliente = Cliente.builder().id(5L).nombre("James").build();
        Pedido pedidoEnRuta = Pedido.builder()
                .id(25L)
                .cliente(cliente)
                .domiciliario(domiciliario)
                .estado("Enviado")
                .fechaCreacion(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        Mockito.when(pedidoService.findByDomiciliario(domiId)).thenReturn(List.of(pedidoEnRuta));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/domiciliario/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(25)))
                .andExpect(jsonPath("$[0].domiciliarioNombre", Matchers.is("Andrés Parra")))
                .andExpect(jsonPath("$[0].estado", Matchers.is("Enviado")));
    }

    /**
     * Consulta 4 [POST]: Crear un nuevo pedido en el restaurante.
     * Envía payload JSON estructurado y espera HTTP 201 CREATED con el PedidoDto creado.
     */
    @Test
    public void pedidoController_ordenarPlatoConAdicionales_retorna201Created() throws Exception {
        // Arrange
        CrearPedidoDto request = CrearPedidoDto.builder()
                .clienteId(1L)
                .items(List.of(
                        CrearPedidoDto.ItemPedidoRequestDto.builder()
                                .comidaId(4L)
                                .cantidad(2)
                                .build()
                ))
                .build();

        Cliente cliente = Cliente.builder().id(1L).nombre("Lucho Díaz").build();
        Pedido nuevoPedido = Pedido.builder()
                .id(50L)
                .cliente(cliente)
                .estado("Cocinando")
                .fechaCreacion(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        Mockito.when(pedidoService.crearPedido(any(CrearPedidoDto.class))).thenReturn(nuevoPedido);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/pedidos/ordenar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(50)))
                .andExpect(jsonPath("$.estado", Matchers.is("Cocinando")))
                .andExpect(jsonPath("$.clienteNombre", Matchers.is("Lucho Díaz")));
    }

    /**
     * Consulta 5 [PUT]: Actualizar el estado de un pedido y gestionar domiciliario.
     * Envía payload JSON de actualización y espera HTTP 200 OK con el estado modificado.
     */
    @Test
    public void pedidoController_actualizarEstadoPedido_retorna200Ok() throws Exception {
        // Arrange
        Long pedidoId = 15L;
        ActualizarEstadoPedidoDto updateDto = ActualizarEstadoPedidoDto.builder()
                .estado("Entregado")
                .domiciliarioId(null)
                .build();

        Cliente cliente = Cliente.builder().id(2L).nombre("Cristiano").build();
        Pedido pedidoEntregado = Pedido.builder()
                .id(pedidoId)
                .cliente(cliente)
                .estado("Entregado")
                .fechaCreacion(LocalDateTime.now().minusHours(1))
                .fechaEntrega(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        Mockito.when(pedidoService.actualizarEstado(eq(pedidoId), eq("Entregado"), any())).thenReturn(pedidoEntregado);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/pedidos/15/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(15)))
                .andExpect(jsonPath("$.estado", Matchers.is("Entregado")))
                .andExpect(jsonPath("$.fechaEntrega", Matchers.notNullValue()));
    }
}
