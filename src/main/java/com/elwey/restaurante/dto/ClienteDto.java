package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.Cliente;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO exclusivo para la entidad Cliente.
 * Maneja datos de perfil, registro y respuestas específicas de clientes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteDto {

    private Long id;
    private String nombre;
    private String apellido;

    @JsonAlias({ "email", "correo" })
    private String correo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias({ "password", "contrasena", "contraseña" })
    private String contrasena;

    private String telefono;
    private String direccion;
    private Boolean activo;
    private String fechaRegistro;

    public static ClienteDto fromEntity(Cliente cliente) {
        if (cliente == null)
            return null;
        return ClienteDto.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .correo(cliente.getEmail())
                .contrasena(null)
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .activo(cliente.getActivo())
                .fechaRegistro(cliente.getFechaRegistro())
                .build();
    }

    public Cliente toEntity() {
        return Cliente.builder()
                .id(this.id)
                .nombre(this.nombre)
                .apellido(this.apellido)
                .email(this.correo)
                .password(this.contrasena)
                .telefono(this.telefono)
                .direccion(this.direccion)
                .activo(this.activo != null ? this.activo : true)
                .fechaRegistro(this.fechaRegistro)
                .build();
    }
}
