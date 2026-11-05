package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.CuponDto;
import com.elwey.restaurante.dto.ValidarCuponRequestDto;
import com.elwey.restaurante.dto.ValidarCuponResponseDto;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de la capa de servicio para la gestión y validación de cupones promocionales.
 */
public interface CuponService {

    /**
     * Retorna el listado de todos los cupones activos de la plataforma.
     */
    List<CuponDto> listarCuponesActivos();

    /**
     * Busca un cupón por su código alfanumérico único.
     */
    Optional<CuponDto> buscarPorCodigo(String codigo);

    /**
     * Valida la aplicabilidad de un cupón sobre un monto de pedido y computa el ahorro.
     */
    ValidarCuponResponseDto validarCupon(ValidarCuponRequestDto request);
}
