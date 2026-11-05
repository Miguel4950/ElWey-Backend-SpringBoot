package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.AdministradorDto;
import com.elwey.restaurante.dto.LoginRequestDto;
import com.elwey.restaurante.service.AdministradorService;
import com.elwey.restaurante.service.ReporteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas MockMvc para AdministradorController (Semana 13).
 * Valida endpoints de autenticación y exportación de reportes CSV.
 * Nomenclatura doctrinal: [controlador]_[metodo]_[lo_que_retorna]
 */
@WebMvcTest(controllers = AdministradorController.class)
@ActiveProfiles("test")
public class AdministradorControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdministradorService administradorService;

    @MockBean
    private ReporteService reporteService;

    @Test
    @DisplayName("administradorController_exportarReportePedidosCsv_returnsCsvAttachment")
    public void administradorController_exportarReportePedidosCsv_returnsCsvAttachment() throws Exception {
        // Arrange
        String mockCsv = "\uFEFFID Pedido;Fecha Creacion;Estado;Cliente;Telefono Cliente;Direccion Entrega;Cantidad Productos;Cantidad Adicionales;Subtotal Comida;Subtotal Adicionales;Total;Domiciliario Asignado\n"
                + "1;2026-11-05 14:00:00;ENTREGADO;Juan Perez;3001112233;Calle 10 # 5-20;2;1;25000;3000;28000;Carlos\n";
        byte[] csvBytes = mockCsv.getBytes(StandardCharsets.UTF_8);

        when(reporteService.generarReportePedidosCsv()).thenReturn(csvBytes);

        // Act & Assert
        mockMvc.perform(get("/admin/reportes/pedidos-csv"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/csv;charset=UTF-8"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("reporte_pedidos_elwey.csv")))
                .andExpect(content().bytes(csvBytes));
    }

    @Test
    @DisplayName("administradorController_login_returnsAdministradorDto")
    public void administradorController_login_returnsAdministradorDto() throws Exception {
        // Arrange
        LoginRequestDto requestDto = new LoginRequestDto("admin@elwey.com", "secreto123");
        AdministradorDto responseDto = AdministradorDto.builder()
                .id(1L)
                .usuario("admin@elwey.com")
                .build();

        when(administradorService.autenticar("admin@elwey.com", "secreto123")).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuario").value("admin@elwey.com"));
    }
}
