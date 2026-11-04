package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.RestauranteInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Implementación del servicio de consulta informativa y estado operativo del restaurante El Wey.
 */
@Slf4j
@Service
public class RestauranteInfoServiceImpl implements RestauranteInfoService {

    private static final String NOMBRE = "El Wey - Taquería Mexicana Auténtica";
    private static final String SLOGAN = "Sabor tradicional y picante con auténtica sazón de barrio";
    private static final String TELEFONO = "+57 312 456 7890";
    private static final String EMAIL = "contacto@elwey-taqueria.com";
    private static final String DIRECCION = "Carrera 7 # 45-23, Zona Gastronómica";
    private static final String CIUDAD = "Bogotá D.C., Colombia";
    private static final String HORARIO_LV = "11:00 AM - 10:00 PM";
    private static final String HORARIO_FINDE = "12:00 PM - 11:30 PM";
    private static final Integer TIEMPO_ESTIMADO = 35;
    private static final Double PEDIDO_MINIMO = 15000.0;

    private static final List<String> ZONAS = Arrays.asList(
            "Chapinero",
            "Teusaquillo",
            "Usaquén",
            "Centro Internacional",
            "Rosales"
    );

    @Override
    public RestauranteInfoDto obtenerInformacion() {
        log.info("Consultando información institucional y estado del restaurante El Wey");

        LocalTime horaActual = LocalTime.now();
        LocalTime apertura = LocalTime.of(11, 0);
        LocalTime cierre = LocalTime.of(23, 30);
        boolean abierto = !horaActual.isBefore(apertura) && !horaActual.isAfter(cierre);

        return RestauranteInfoDto.builder()
                .nombre(NOMBRE)
                .slogan(SLOGAN)
                .telefono(TELEFONO)
                .email(EMAIL)
                .direccion(DIRECCION)
                .ciudad(CIUDAD)
                .horarioLunesViernes(HORARIO_LV)
                .horarioFinDeSemana(HORARIO_FINDE)
                .abiertoAhora(abierto)
                .tiempoEstimadoEntregaMinutos(TIEMPO_ESTIMADO)
                .pedidoMinimoCop(PEDIDO_MINIMO)
                .zonasCobertura(ZONAS)
                .build();
    }
}
