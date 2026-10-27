package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.Administrador;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO exclusivo para la entidad Administrador.
 * Maneja datos de autenticación del administrador sin exponer su contraseña.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdministradorDto {

    private Long id;

    @JsonAlias({ "usuario", "correo", "email" })
    private String usuario;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias({ "password", "contrasena", "contraseña" })
    private String contrasena;

    public static AdministradorDto fromEntity(Administrador administrador) {
        if (administrador == null)
            return null;
        return AdministradorDto.builder()
                .id(administrador.getId())
                .usuario(administrador.getUsuario())
                .contrasena(null)
                .build();
    }

    public Administrador toEntity() {
        return Administrador.builder()
                .id(this.id)
                .usuario(this.usuario)
                .contrasena(this.contrasena)
                .build();
    }
}
