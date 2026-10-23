package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.Administrador;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Operador;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO para la gestión y autenticación de usuarios.
 * Devuelve información de perfil y el rol explícito ('CLIENTE', 'OPERADOR', 'ADMIN')
 * ocultando siempre la contraseña en respuestas salientes.
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

    @JsonAlias({ "email", "correo", "usuario" })
    private String correo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias({ "password", "contrasena", "contraseña" })
    private String contrasena;

    private String telefono;
    private String direccion;

    @Builder.Default
    private Boolean activo = true;

    private String fechaRegistro;

    @Builder.Default
    private String rol = "CLIENTE";

    public static ClienteDto fromEntity(Cliente cliente) {
        return fromCliente(cliente);
    }

    public static ClienteDto fromCliente(Cliente cliente) {
        if (cliente == null) return null;
        String rolDeterminado = "CLIENTE";
        if (cliente.getEmail() != null) {
            String lower = cliente.getEmail().toLowerCase();
            if (lower.startsWith("admin") || lower.contains("admin@")) {
                rolDeterminado = "ADMIN";
            } else if (lower.startsWith("operador") || lower.contains("operador@")) {
                rolDeterminado = "OPERADOR";
            }
        }

        return ClienteDto.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .correo(cliente.getEmail())
                .contrasena(null) // Nunca enviar la contraseña a Angular
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .activo(cliente.getActivo())
                .fechaRegistro(cliente.getFechaRegistro())
                .rol(rolDeterminado)
                .build();
    }

    public static ClienteDto fromOperador(Operador op) {
        if (op == null) return null;
        return ClienteDto.builder()
                .id(op.getId())
                .nombre(op.getNombre())
                .correo(op.getUsuario())
                .contrasena(null)
                .telefono("+57 300 000 0002")
                .direccion("Sede Cocina y Despachos, Bogotá")
                .activo(true)
                .fechaRegistro("2026-01-01")
                .rol("OPERADOR")
                .build();
    }

    public static ClienteDto fromAdministrador(Administrador adm) {
        if (adm == null) return null;
        return ClienteDto.builder()
                .id(adm.getId())
                .nombre("Administrador (" + adm.getUsuario() + ")")
                .correo(adm.getUsuario())
                .contrasena(null)
                .telefono("+57 300 000 0001")
                .direccion("Sede Administrativa El Wey, Bogotá")
                .activo(true)
                .fechaRegistro("2026-01-01")
                .rol("ADMIN")
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
                .fechaRegistro(this.fechaRegistro)
                .activo(this.activo != null ? this.activo : true)
                .build();
    }
}
