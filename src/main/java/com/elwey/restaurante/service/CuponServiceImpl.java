package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.CuponDto;
import com.elwey.restaurante.dto.ValidarCuponRequestDto;
import com.elwey.restaurante.dto.ValidarCuponResponseDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación de la capa de servicio para cupones promocionales del restaurante.
 * Mantiene el catálogo en memoria de campañas comerciales de "El Wey".
 */
@Service
public class CuponServiceImpl implements CuponService {

    private final Map<String, CuponDto> catalogoCupones = new LinkedHashMap<>();

    public CuponServiceImpl() {
        inicializarCupones();
    }

    private void inicializarCupones() {
        catalogoCupones.put("ELWEY10", CuponDto.builder()
                .codigo("ELWEY10")
                .descripcion("10% de descuento en tu orden")
                .tipo("porcentaje")
                .valor(10)
                .compraMinima(15000)
                .activo(true)
                .build());

        catalogoCupones.put("TACOLOVER", CuponDto.builder()
                .codigo("TACOLOVER")
                .descripcion("15% de descuento especial para amantes de los tacos")
                .tipo("porcentaje")
                .valor(15)
                .compraMinima(25000)
                .activo(true)
                .build());

        catalogoCupones.put("PROMO5K", CuponDto.builder()
                .codigo("PROMO5K")
                .descripcion("$5.000 COP de descuento directo en tu orden")
                .tipo("fijo")
                .valor(5000)
                .compraMinima(30000)
                .activo(true)
                .build());

        catalogoCupones.put("BIENVENIDO", CuponDto.builder()
                .codigo("BIENVENIDO")
                .descripcion("$3.000 COP de regalo de bienvenida para tu primer pedido")
                .tipo("fijo")
                .valor(3000)
                .compraMinima(20000)
                .activo(true)
                .build());
    }

    @Override
    public List<CuponDto> listarCuponesActivos() {
        return catalogoCupones.values().stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CuponDto> buscarPorCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        String normalizado = codigo.trim().toUpperCase();
        return Optional.ofNullable(catalogoCupones.get(normalizado));
    }

    @Override
    public ValidarCuponResponseDto validarCupon(ValidarCuponRequestDto request) {
        if (request == null || request.getCodigo() == null) {
            return ValidarCuponResponseDto.builder()
                    .valido(false)
                    .codigo("")
                    .descuentoCalculado(0)
                    .totalFinal(0)
                    .mensaje("Debes suministrar un código de cupón.")
                    .build();
        }

        int monto = request.getMontoPedido() != null ? Math.max(0, request.getMontoPedido()) : 0;
        Optional<CuponDto> opt = buscarPorCodigo(request.getCodigo());

        if (opt.isEmpty() || Boolean.FALSE.equals(opt.get().getActivo())) {
            return ValidarCuponResponseDto.builder()
                    .valido(false)
                    .codigo(request.getCodigo().toUpperCase())
                    .descuentoCalculado(0)
                    .totalFinal(monto)
                    .mensaje("¡Órale! El cupón no es válido o ha expirado.")
                    .build();
        }

        CuponDto cupon = opt.get();
        if (monto < cupon.getCompraMinima()) {
            return ValidarCuponResponseDto.builder()
                    .valido(false)
                    .codigo(cupon.getCodigo())
                    .descuentoCalculado(0)
                    .totalFinal(monto)
                    .mensaje(String.format("Este cupón exige una compra mínima de $%d COP.", cupon.getCompraMinima()))
                    .build();
        }

        int descuento = 0;
        if ("porcentaje".equalsIgnoreCase(cupon.getTipo())) {
            descuento = Math.round((monto * cupon.getValor()) / 100.0f);
        } else {
            descuento = Math.min(monto, cupon.getValor());
        }

        int totalConDescuento = Math.max(0, monto - descuento);

        return ValidarCuponResponseDto.builder()
                .valido(true)
                .codigo(cupon.getCodigo())
                .descuentoCalculado(descuento)
                .totalFinal(totalConDescuento)
                .mensaje(String.format("¡Cupón %s activado! %s", cupon.getCodigo(), cupon.getDescripcion()))
                .build();
    }
}
