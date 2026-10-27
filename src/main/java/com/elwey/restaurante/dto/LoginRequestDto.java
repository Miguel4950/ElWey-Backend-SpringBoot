package com.elwey.restaurante.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    @JsonAlias({ "email", "correo", "usuario", "cedula", "celular", "identificador" })
    private String correo;

    @JsonAlias({ "password", "contrasena", "contraseña" })
    private String contrasena;
}
