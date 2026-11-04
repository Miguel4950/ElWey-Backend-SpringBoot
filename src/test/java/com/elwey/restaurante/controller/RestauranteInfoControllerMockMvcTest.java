package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.RestauranteInfoDto;
import com.elwey.restaurante.service.RestauranteInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias de capa Web para RestauranteInfoController con MockMvc y aislamiento de servicio.
 * Cumple con la nomenclatura doctrinal de cátedra: [componente]_[metodo]_[lo_que_retorna].
 */
@WebMvcTest(RestauranteInfoController.class)
@ActiveProfiles("test")
public class RestauranteInfoControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestauranteInfoService restauranteInfoService;

    @Test
    @DisplayName("RestauranteInfoController_obtenerInformacion_retornaInformacionY200")
    void restauranteInfoController_obtenerInformacion_retornaInformacionY200() throws Exception {
        RestauranteInfoDto dtoEsperado = RestauranteInfoDto.builder()
                .nombre("El Wey - Taquería Mexicana Auténtica")
                .slogan("Sabor tradicional y picante con auténtica sazón de barrio")
                .telefono("+57 312 456 7890")
                .email("contacto@elwey-taqueria.com")
                .direccion("Carrera 7 # 45-23, Zona Gastronómica")
                .ciudad("Bogotá D.C., Colombia")
                .horarioLunesViernes("11:00 AM - 10:00 PM")
                .horarioFinDeSemana("12:00 PM - 11:30 PM")
                .abiertoAhora(true)
                .tiempoEstimadoEntregaMinutos(35)
                .pedidoMinimoCop(15000.0)
                .zonasCobertura(Arrays.asList("Chapinero", "Teusaquillo"))
                .build();

        Mockito.when(restauranteInfoService.obtenerInformacion()).thenReturn(dtoEsperado);

        mockMvc.perform(MockMvcRequestBuilders.get("/restaurante/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("El Wey - Taquería Mexicana Auténtica"))
                .andExpect(jsonPath("$.telefono").value("+57 312 456 7890"))
                .andExpect(jsonPath("$.ciudad").value("Bogotá D.C., Colombia"))
                .andExpect(jsonPath("$.abiertoAhora").value(true))
                .andExpect(jsonPath("$.tiempoEstimadoEntregaMinutos").value(35))
                .andExpect(jsonPath("$.pedidoMinimoCop").value(15000.0))
                .andExpect(jsonPath("$.zonasCobertura[0]").value("Chapinero"));
    }
}
