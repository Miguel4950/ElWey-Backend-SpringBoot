package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.ActualizarEstadoPedidoDto;
import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.errors.PedidoNotFoundException;
import com.elwey.restaurante.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
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
 * Utiliza @WebMvcTest, @RunWith(SpringRunner.class) y .value() en jsonPath según la doctrina del docente.
 * Cubre GET colección, GET por ID, GET con filtro, POST crear, PUT actualizar y DELETE eliminar.
 */
@RunWith(SpringRunner.class)
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
     * Consulta 1 [GET Colección]: Listar todos los pedidos registrados en el restaurante.
     * Espera HTTP 200 OK y una lista JSON serializada con sus atributos correspondientes.
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
     * Consulta 2 [GET por ID]: Obtener el detalle consolidado de un pedido por su identificador.
     * Valida respuesta HTTP 200 OK ante ID existente y HTTP 404 NOT FOUND ante ID no registrado.
     */
    @Test
    public void pedidoController_mostrarPedidoPorId_returnsPedido() throws Exception {
        // Arrange
        Cliente cliente = Cliente.builder().id(3L).nombre("Falcao").email("falcao@elwey.com").build();
        Pedido pedido = Pedido.builder().id(10L).cliente(cliente).estado("Cocinando").fechaCreacion(LocalDateTime.now()).items(new ArrayList<>()).build();

        Mockito.when(pedidoService.searchById(10L)).thenReturn(pedido);
        Mockito.when(pedidoService.searchById(999L)).thenThrow(new PedidoNotFoundException(999L));

        // Act & Assert 1: Pedido existente -> 200 OK
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.estado").value("Cocinando"))
                .andExpect(jsonPath("$.clienteNombre").value("Falcao"));

        // Act & Assert 2: Pedido inexistente -> 404 NOT FOUND
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No se pudo encontrar el pedido con ID: 999"));
    }

    /**
     * Consulta 3 [GET con Filtro]: Consultar los pedidos asignados a un domiciliario específico.
     * Espera HTTP 200 OK y la lista de pedidos en ruta para dicho repartidor.
     */
    @Test
    public void pedidoController_mostrarPedidosPorDomiciliario_returnsFilteredList() throws Exception {
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
     * Consulta 4 [POST Creación]: Crear un nuevo pedido en el restaurante.
     * Valida HTTP 201 CREATED ante payload válido y HTTP 400 BAD REQUEST ante payload con ítems vacíos.
     */
    @Test
    public void pedidoController_ordenarPlatoConAdicionales_returnsCreated() throws Exception {
        // Arrange: Pedido válido
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

        // Act & Assert 1: Creación exitosa -> 201 CREATED
        mockMvc.perform(MockMvcRequestBuilders.post("/pedidos/ordenar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.estado").value("Cocinando"))
                .andExpect(jsonPath("$.clienteNombre").value("Lucho Díaz"));

        // Act & Assert 2: Payload inválido (items vacíos) -> 400 BAD REQUEST
        CrearPedidoDto requestInvalido = CrearPedidoDto.builder()
                .clienteId(1L)
                .items(List.of())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/pedidos/ordenar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Consulta 5 [PUT Actualización]: Actualizar el estado de un pedido y gestionar domiciliario.
     * Envía payload JSON de actualización y espera HTTP 200 OK con el estado modificado.
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
     * Consulta 6 [DELETE Eliminación]: Eliminar un pedido por su identificador.
     * Valida respuesta HTTP 204 NO CONTENT cuando se ejecuta con éxito.
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
}
