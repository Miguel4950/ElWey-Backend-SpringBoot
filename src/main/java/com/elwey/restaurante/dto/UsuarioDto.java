package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.Administrador;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.Operador;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * DTO general para representar cualquier usuario autenticado en el sistema
 * independientemente de su rol (CLIENTE, OPERADOR, ADMIN, DOMICILIARIO).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String identificador;
    private String correo;
    private String telefono;
    private String direccion;
    private Boolean activo;
    private String rol;

    public static UsuarioDto fromCliente(Cliente cliente) {
        if (cliente == null) return null;
        return UsuarioDto.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .correo(cliente.getEmail())
                .identificador(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .activo(cliente.getActivo())
                .rol("CLIENTE")
                .build();
    }

    public static UsuarioDto fromOperador(Operador op) {
        if (op == null) return null;
        return UsuarioDto.builder()
                .id(op.getId())
                .nombre(op.getNombre())
                .identificador(op.getUsuario())
                .correo(op.getUsuario())
                .activo(true)
                .rol("OPERADOR")
                .build();
    }

    public static UsuarioDto fromAdministrador(Administrador adm) {
        if (adm == null) return null;
        return UsuarioDto.builder()
                .id(adm.getId())
                .nombre("Administrador (" + adm.getUsuario() + ")")
                .identificador(adm.getUsuario())
                .correo(adm.getUsuario())
                .activo(true)
                .rol("ADMIN")
                .build();
    }

    public static UsuarioDto fromDomiciliario(Domiciliario dom) {
        if (dom == null) return null;
        return UsuarioDto.builder()
                .id(dom.getId())
                .nombre(dom.getNombre())
                .identificador(dom.getCedula())
                .telefono(dom.getCelular())
                .activo(dom.getDisponible())
                .rol("DOMICILIARIO")
                .build();
    }
}

