package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.CrearPedidoDto;
import com.elwey.restaurante.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PedidoController.class)
@ActiveProfiles("test")
public class ValidacionDtoMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /pedidos/ordenar con payload inválido retorna 400 Bad Request y mensaje estructurado")
    public void pedidoController_ordenar_retorna400ConMensajeValidacion() throws Exception {
        // Arrange
        CrearPedidoDto dtoInvalido = CrearPedidoDto.builder()
                .clienteId(null)
                .items(new ArrayList<>())
                .build();

        // Act & Assert
        mockMvc.perform(post("/pedidos/ordenar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }
}
