package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.CuponDto;
import com.elwey.restaurante.dto.ValidarCuponRequestDto;
import com.elwey.restaurante.dto.ValidarCuponResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

/**
 * Pruebas unitarias para CuponService (Semana 13).
 * Sigue la doctrina estricta de Juan Sebastián Angarita:
 * - JUnit 5 Jupiter con MockitoExtension
 * - Nomenclatura: cuponService_[metodo]_[retorno]
 * - Patrón AAA (Arrange, Act, Assert)
 * - Escenarios simétricos de éxito vs casos límite
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CuponServiceTestMock {

    private CuponServiceImpl cuponService;

    @BeforeEach
    public void setUp() {
        cuponService = new CuponServiceImpl();
    }

    @Test
    public void cuponService_listarCuponesActivos_retornaListaCupones() {
        // Arrange (Configuración inicial en setUp)

        // Act
        List<CuponDto> cupones = cuponService.listarCuponesActivos();

        // Assert
        Assertions.assertThat(cupones).isNotNull();
        Assertions.assertThat(cupones).isNotEmpty();
        Assertions.assertThat(cupones.size()).isGreaterThanOrEqualTo(4);
    }

    @Test
    public void cuponService_buscarPorCodigo_retornaCuponExistente() {
        // Arrange
        String codigoBuscado = "ELWEY10";

        // Act
        Optional<CuponDto> resultado = cuponService.buscarPorCodigo(codigoBuscado);

        // Assert
        Assertions.assertThat(resultado).isPresent();
        Assertions.assertThat(resultado.get().getCodigo()).isEqualTo("ELWEY10");
        Assertions.assertThat(resultado.get().getTipo()).isEqualTo("porcentaje");
        Assertions.assertThat(resultado.get().getValor()).isEqualTo(10);
    }

    @Test
    public void cuponService_buscarPorCodigo_retornaOptionalVacioCuandoNoExiste() {
        // Arrange
        String codigoInexistente = "CUPONFALSO99";

        // Act
        Optional<CuponDto> resultado = cuponService.buscarPorCodigo(codigoInexistente);

        // Assert
        Assertions.assertThat(resultado).isEmpty();
    }

    @Test
    public void cuponService_validarCupon_retornaExitoConDescuentoPorcentual() {
        // Arrange
        ValidarCuponRequestDto request = ValidarCuponRequestDto.builder()
                .codigo("ELWEY10")
                .montoPedido(50000)
                .build();

        // Act
        ValidarCuponResponseDto response = cuponService.validarCupon(request);

        // Assert
        Assertions.assertThat(response.getValido()).isTrue();
        Assertions.assertThat(response.getCodigo()).isEqualTo("ELWEY10");
        Assertions.assertThat(response.getDescuentoCalculado()).isEqualTo(5000);
        Assertions.assertThat(response.getTotalFinal()).isEqualTo(45000);
    }

    @Test
    public void cuponService_validarCupon_retornaInvalidoPorMontoMinimoInsuficiente() {
        // Arrange (ELWEY10 requiere mínimo $15.000)
        ValidarCuponRequestDto request = ValidarCuponRequestDto.builder()
                .codigo("ELWEY10")
                .montoPedido(8000)
                .build();

        // Act
        ValidarCuponResponseDto response = cuponService.validarCupon(request);

        // Assert
        Assertions.assertThat(response.getValido()).isFalse();
        Assertions.assertThat(response.getDescuentoCalculado()).isEqualTo(0);
        Assertions.assertThat(response.getTotalFinal()).isEqualTo(8000);
        Assertions.assertThat(response.getMensaje()).contains("compra mínima");
    }

    @Test
    public void cuponService_validarCupon_retornaInvalidoCuandoCuponNoExiste() {
        // Arrange
        ValidarCuponRequestDto request = ValidarCuponRequestDto.builder()
                .codigo("NOEXISTE")
                .montoPedido(60000)
                .build();

        // Act
        ValidarCuponResponseDto response = cuponService.validarCupon(request);

        // Assert
        Assertions.assertThat(response.getValido()).isFalse();
        Assertions.assertThat(response.getDescuentoCalculado()).isEqualTo(0);
        Assertions.assertThat(response.getTotalFinal()).isEqualTo(60000);
        Assertions.assertThat(response.getMensaje()).contains("no es válido");
    }
}
