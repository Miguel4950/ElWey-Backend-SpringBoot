package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.CuponDto;
import com.elwey.restaurante.dto.ValidarCuponRequestDto;
import com.elwey.restaurante.dto.ValidarCuponResponseDto;
import com.elwey.restaurante.service.CuponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la consulta y validación de cupones promocionales de "El Wey".
 */
@Tag(name = "Cupones Promocionales", description = "Endpoints de marketing gastronómico y canje de cupones")
@RestController
@RequestMapping("/cupones")
@CrossOrigin(origins = "http://localhost:4200")
public class CuponController {

    private static final Logger log = LoggerFactory.getLogger(CuponController.class);

    @Autowired
    private CuponService cuponService;

    @Operation(summary = "Listar todos los cupones activos del restaurante")
    @GetMapping({ "", "/" })
    public ResponseEntity<List<CuponDto>> listarCuponesActivos() {
        log.info("Petición REST: Listar cupones promocionales activos");
        return ResponseEntity.ok(cuponService.listarCuponesActivos());
    }

    @Operation(summary = "Consultar cupón por su código promocional")
    @GetMapping("/{codigo}")
    public ResponseEntity<CuponDto> buscarPorCodigo(@PathVariable String codigo) {
        log.info("Petición REST: Consultar cupón con código: {}", codigo);
        return cuponService.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Validar cupón y calcular descuento sobre un pedido")
    @PostMapping("/validar")
    public ResponseEntity<ValidarCuponResponseDto> validarCupon(@RequestBody ValidarCuponRequestDto request) {
        log.info("Petición REST: Validar cupón: {} para monto: {}", 
                request != null ? request.getCodigo() : "null",
                request != null ? request.getMontoPedido() : "null");
        ValidarCuponResponseDto response = cuponService.validarCupon(request);
        return ResponseEntity.ok(response);
    }
}
