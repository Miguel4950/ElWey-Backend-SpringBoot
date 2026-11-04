package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.RestauranteInfoDto;

/**
 * Contrato de servicio para consultar la información pública y operativa de El Wey.
 */
public interface RestauranteInfoService {

    RestauranteInfoDto obtenerInformacion();
}
