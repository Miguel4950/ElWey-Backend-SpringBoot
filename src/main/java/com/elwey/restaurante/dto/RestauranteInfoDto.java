package com.elwey.restaurante.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO para consultar la información pública y estado operativo del restaurante El Wey.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestauranteInfoDto {

    private String nombre;
    private String slogan;
    private String telefono;
    private String email;
    private String direccion;
    private String ciudad;
    private String horarioLunesViernes;
    private String horarioFinDeSemana;
    private boolean abiertoAhora;
    private Integer tiempoEstimadoEntregaMinutos;
    private Double pedidoMinimoCop;
    private List<String> zonasCobertura;
}
