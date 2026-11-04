package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.ActualizarEstadoPedidoDto;
import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.errors.PedidoNotFoundException;
import com.elwey.restaurante.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
 * Utiliza JUnit 5 Jupiter, imports estáticos de MockMvc (get, post, put, delete, status, jsonPath)
 * según la doctrina del docente y los ajustes de Jaimes.
 * Nomenclatura doctrinal: [componente]_[metodo]_[lo_que_retorna]
 * Cada endpoint separa estrictamente el caso positivo y el caso de error/vacío en métodos independientes.
 */
@WebMvcTest(controllers = PedidoController.class)
@ActiveProfiles("test")
public class PedidoControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Endpoint 1 [GET /pedidos - Caso Positivo]: Listar todos los pedidos cuando existen registros.
     * Retorna una lista llena (returnsList) en formato JSON con código HTTP 200 OK.
     */
    @Test
    public void pedidoController_mostrarPedidos_returnsList() throws Exception {
        // Arrange
        Cliente cliente = Cliente.builder().id(1L).nombre("Messi").email("messi@elwey.com").build();
        Pedido p1 = Pedido.builder().id(1L).cliente(cliente).estado("Cocinando").fechaCreacion(LocalDateTime.now()).items(new ArrayList<>()).build();
        Pedido p2 = Pedido.builder().id(2L).cliente(cliente).estado("Enviado").fechaCreacion(LocalDateTime.now()).items(new ArrayList<>()).build();

        Mockito.when(pedidoService.searchAll()).thenReturn(List.of(p1, p2));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").value("Cocinando"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].estado").value("Enviado"));
    }

    /**
     * Endpoint 1 [GET /pedidos - Caso Vacío]: Listar pedidos cuando el restaurante no tiene órdenes activas.
     * Retorna una lista vacía (returnsEmpty) con código HTTP 200 OK.
     */
    @Test
    public void pedidoController_mostrarPedidos_returnsEmpty() throws Exception {
        // Arrange
        Mockito.when(pedidoService.searchAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Endpoint 2 [GET /pedidos/{id} - Caso Positivo]: Obtener el detalle de un pedido existente.
     * Retorna el objeto Pedido con código HTTP 200 OK.
     */
    @Test
    public void pedidoController_mostrarPedidoPorId_returnsPedido() throws Exception {
        // Arrange
        Cliente cliente = Cliente.builder().id(3L).nombre("Falcao").email("falcao@elwey.com").build();
        Pedido pedido = Pedido.builder().id(10L).cliente(cliente).estado("Cocinando").fechaCreacion(LocalDateTime.now()).items(new ArrayList<>()).build();

        Mockito.when(pedidoService.searchById(10L)).thenReturn(pedido);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.estado").value("Cocinando"))
                .andExpect(jsonPath("$.clienteNombre").value("Falcao"));
    }

    /**
     * Endpoint 2 [GET /pedidos/{id} - Caso Negativo]: Buscar un pedido inexistente por ID.
     * Retorna NotFound (returnsNotFound) con código HTTP 404 NOT FOUND.
     */
    @Test
    public void pedidoController_mostrarPedidoPorId_returnsNotFound() throws Exception {
        // Arrange
        Mockito.when(pedidoService.searchById(999L)).thenThrow(new PedidoNotFoundException(999L));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No se pudo encontrar el pedido con ID: 999"));
    }

    /**
     * Endpoint 3 [GET /pedidos/domiciliario/{id} - Caso Positivo]: Consultar pedidos asignados a un repartidor con despachos.
     * Retorna una lista llena (returnsList) de pedidos para dicho repartidor con código HTTP 200 OK.
     */
    @Test
    public void pedidoController_mostrarPedidosPorDomiciliario_returnsList() throws Exception {
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
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(25))
                .andExpect(jsonPath("$[0].domiciliarioNombre").value("Andrés Parra"))
                .andExpect(jsonPath("$[0].estado").value("Enviado"));
    }

    /**
     * Endpoint 3 [GET /pedidos/domiciliario/{id} - Caso Vacío]: Consultar pedidos de un repartidor libre sin entregas asignadas.
     * Retorna una lista vacía (returnsEmpty) con código HTTP 200 OK.
     */
    @Test
    public void pedidoController_mostrarPedidosPorDomiciliario_returnsEmpty() throws Exception {
        // Arrange
        Long domiId = 3L;
        Mockito.when(pedidoService.findByDomiciliario(domiId)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/domiciliario/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Endpoint 4 [POST /pedidos/ordenar - Caso Positivo]: Crear un nuevo pedido en el restaurante.
     * Retorna el estado Created (returnsCreated) con código HTTP 201 CREATED.
     */
    @Test
    public void pedidoController_crearPedido_returnsCreated() throws Exception {
        // Arrange
        CrearPedidoDto requestValido = CrearPedidoDto.builder()
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
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.estado").value("Cocinando"))
                .andExpect(jsonPath("$.clienteNombre").value("Lucho Díaz"));
    }

    /**
     * Endpoint 5 [PUT /pedidos/{id}/estado - Caso Positivo]: Actualizar el estado de un pedido y gestionar domiciliario.
     * Retorna el objeto Pedido modificado con código HTTP 200 OK.
     */
    @Test
    public void pedidoController_actualizarEstadoPedido_returnsOk() throws Exception {
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
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.estado").value("Entregado"));
    }

    /**
     * Endpoint 5 [PUT /pedidos/{id}/estado - Caso Negativo]: Intentar actualizar un pedido inexistente.
     * Retorna NotFound (returnsNotFound) con código HTTP 404 NOT FOUND.
     */
    @Test
    public void pedidoController_actualizarEstadoPedido_returnsNotFound() throws Exception {
        // Arrange
        Long idInexistente = 888L;
        ActualizarEstadoPedidoDto updateDto = ActualizarEstadoPedidoDto.builder()
                .estado("Enviado")
                .domiciliarioId(1L)
                .build();

        Mockito.when(pedidoService.actualizarEstado(eq(idInexistente), eq("Enviado"), eq(1L)))
                .thenThrow(new PedidoNotFoundException(idInexistente));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/pedidos/888/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No se pudo encontrar el pedido con ID: 888"));
    }

    /**
     * Endpoint 6 [DELETE /pedidos/{id} - Caso Eliminación]: Eliminar un pedido por su identificador.
     * Retorna vacío (returnsEmpty) con código HTTP 204 NO CONTENT cuando se ejecuta con éxito.
     */
    @Test
    public void pedidoController_eliminarPedido_returnsNoContent() throws Exception {
        // Arrange
        Long id = 1L;
        Mockito.doNothing().when(pedidoService).delete(id);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/pedidos/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(pedidoService).delete(id);
    }

    @Test
    public void pedidoController_filtrarPedidosPorFechas_returnsOk() throws Exception {
        // Arrange
        Pedido p = Pedido.builder()
                .id(10L)
                .estado("Listo")
                .fechaCreacion(LocalDateTime.of(2026, 11, 2, 10, 0))
                .cliente(Cliente.builder().id(1L).nombre("Carlos").apellido("Vela").build())
                .items(new ArrayList<>())
                .build();
        Mockito.when(pedidoService.buscarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(p));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/fechas")
                        .param("inicio", "2026-11-01T00:00:00")
                        .param("fin", "2026-11-04T23:59:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].estado").value("Listo"));
    }
}
