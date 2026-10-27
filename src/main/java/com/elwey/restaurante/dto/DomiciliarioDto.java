package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.Domiciliario;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO exclusivo para la entidad Domiciliario.
 * Maneja datos de registro, disponibilidad y respuestas de domiciliarios sin exponer su contraseña.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomiciliarioDto {

    private Long id;
    private String cedula;
    private String nombre;
    private String celular;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias({ "password", "contrasena", "contraseña" })
    private String contrasena;

    private Boolean disponible;

    public static DomiciliarioDto fromEntity(Domiciliario domiciliario) {
        if (domiciliario == null)
            return null;
        return DomiciliarioDto.builder()
                .id(domiciliario.getId())
                .cedula(domiciliario.getCedula())
                .nombre(domiciliario.getNombre())
                .celular(domiciliario.getCelular())
                .contrasena(null)
                .disponible(domiciliario.getDisponible())
                .build();
    }

    public Domiciliario toEntity() {
        return Domiciliario.builder()
                .id(this.id)
                .cedula(this.cedula)
                .nombre(this.nombre)
                .celular(this.celular)
                .contrasena(this.contrasena)
                .disponible(this.disponible)
                .build();
    }
}
