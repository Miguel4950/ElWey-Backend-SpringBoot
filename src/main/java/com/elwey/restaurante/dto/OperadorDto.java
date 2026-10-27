package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.Operador;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO exclusivo para la entidad Operador.
 * Maneja datos de registro, edición y respuestas de operadores sin exponer su contraseña.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperadorDto {

    private Long id;
    private String nombre;

    @JsonAlias({ "usuario", "correo", "email" })
    private String usuario;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias({ "password", "contrasena", "contraseña" })
    private String contrasena;

    public static OperadorDto fromEntity(Operador operador) {
        if (operador == null)
            return null;
        return OperadorDto.builder()
                .id(operador.getId())
                .nombre(operador.getNombre())
                .usuario(operador.getUsuario())
                .contrasena(null)
                .build();
    }

    public Operador toEntity() {
        return Operador.builder()
                .id(this.id)
                .nombre(this.nombre)
                .usuario(this.usuario)
                .contrasena(this.contrasena)
                .build();
    }
}
