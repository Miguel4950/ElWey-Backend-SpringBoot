package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.AdministradorDto;

/**
 * Interfaz de la capa de Servicio de Negocio para Administrador (únicamente autenticación).
 */
public interface AdministradorService {

    AdministradorDto autenticar(String usuario, String contrasena);
}
