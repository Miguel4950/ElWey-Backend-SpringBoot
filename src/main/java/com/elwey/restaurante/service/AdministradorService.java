package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.AdministradorDto;
import com.elwey.restaurante.dto.ResumenEstadisticasDto;

/**
 * Interfaz de la capa de Servicio de Negocio para Administrador.
 */
public interface AdministradorService {

    AdministradorDto autenticar(String usuario, String contrasena);

    ResumenEstadisticasDto obtenerResumenEstadisticas();
}
