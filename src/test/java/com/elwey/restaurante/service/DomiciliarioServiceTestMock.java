package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.errors.DomiciliarioConPedidoEnviadoException;
import com.elwey.restaurante.repository.DomiciliarioRepository;
import com.elwey.restaurante.repository.PedidoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Pruebas unitarias de Servicio con Mocks para Domiciliario (Semana 13).
 * Sigue la doctrina del profesor Angarita (CourseServiceTest):
 * - @ExtendWith(MockitoExtension.class)
 * - @ActiveProfiles("test")
 * - Nomenclatura [componente]_[metodo]_[lo_que_retorna]
 * - Aislamiento estricto de repositorios mediante simulación Mockito.
 * - Separación de pruebas de éxito y fallo en métodos independientes.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class DomiciliarioServiceTestMock {

    @Mock
    private DomiciliarioRepository domiciliarioRepo;

    @Mock
    private PedidoRepository pedidoRepo;

    @InjectMocks
    private DomiciliarioServiceImpl domiciliarioService;

    /**
     * Prueba 1 [Regla de Negocio - Caso Conflicto/Fallo]: Si se intenta poner libre (disponible = true) a un domiciliario
     * que se encuentra en un pedido "Enviado", el servicio DEBE lanzar DomiciliarioConPedidoEnviadoException
     * (retorna conflicto de validación) y NO guardar cambios en la base de datos.
     */
    @Test
    public void domiciliarioService_actualizarDisponibilidad_returnsConflict() {
        // Arrange
        Long domiId = 1L;
        Domiciliario domiOcupado = Domiciliario.builder()
                .id(domiId)
                .nombre("Carlos Gómez")
                .cedula("1010203040")
                .celular("+57 311 222 3333")
                .disponible(false)
                .build();

        Pedido pedidoEnviado = Pedido.builder()
                .id(101L)
                .estado("Enviado")
                .domiciliario(domiOcupado)
                .build();

        Mockito.when(domiciliarioRepo.findById(domiId)).thenReturn(Optional.of(domiOcupado));
        Mockito.when(pedidoRepo.findByDomiciliarioId(domiId)).thenReturn(List.of(pedidoEnviado));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> domiciliarioService.actualizarDisponibilidad(domiId, true))
                .isInstanceOf(DomiciliarioConPedidoEnviadoException.class)
                .hasMessageContaining("No se puede marcar como libre/disponible al domiciliario")
                .hasMessageContaining("Enviado (en ruta de despacho)");

        verify(domiciliarioRepo, never()).save(any());
    }

    /**
     * Prueba 2 [Regla de Negocio - Caso Éxito]: Si el domiciliario no tiene pedidos en estado "Enviado" (por ejemplo, pedidos Entregados
     * o lista vacía), se permite marcarlo como libre / disponible exitosamente retornando el Domiciliario actualizado.
     */
    @Test
    public void domiciliarioService_actualizarDisponibilidad_Domiciliario() {
        // Arrange
        Long domiId = 2L;
        Domiciliario domiOcupado = Domiciliario.builder()
                .id(domiId)
                .nombre("Andrés Parra")
                .cedula("1020304050")
                .celular("+57 312 444 5555")
                .disponible(false)
                .build();

        Pedido pedidoEntregado = Pedido.builder()
                .id(102L)
                .estado("Entregado")
                .domiciliario(domiOcupado)
                .build();

        Domiciliario domiDisponible = Domiciliario.builder()
                .id(domiId)
                .nombre("Andrés Parra")
                .cedula("1020304050")
                .celular("+57 312 444 5555")
                .disponible(true)
                .build();

        Mockito.when(domiciliarioRepo.findById(domiId)).thenReturn(Optional.of(domiOcupado));
        Mockito.when(pedidoRepo.findByDomiciliarioId(domiId)).thenReturn(List.of(pedidoEntregado));
        Mockito.when(domiciliarioRepo.save(any(Domiciliario.class))).thenReturn(domiDisponible);

        // Act
        Domiciliario resultado = domiciliarioService.actualizarDisponibilidad(domiId, true);

        // Assert
        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.getDisponible()).isTrue();
        verify(domiciliarioRepo).save(any(Domiciliario.class));
    }

    /**
     * Prueba 3 [Caso Éxito Ocupado]: Marcar como ocupado (disponible = false) a un domiciliario libre
     * siempre es una operación permitida sin restricciones de pedidos en ruta, retornando el Domiciliario actualizado.
     */
    @Test
    public void domiciliarioService_actualizarDisponibilidad_marcarOcupado_Domiciliario() {
        // Arrange
        Long domiId = 3L;
        Domiciliario domiLibre = Domiciliario.builder()
                .id(domiId)
                .nombre("Mateo Quintana")
                .cedula("1030405060")
                .celular("+57 315 666 7777")
                .disponible(true)
                .build();

        Domiciliario domiOcupado = Domiciliario.builder()
                .id(domiId)
                .nombre("Mateo Quintana")
                .cedula("1030405060")
                .celular("+57 315 666 7777")
                .disponible(false)
                .build();

        Mockito.when(domiciliarioRepo.findById(domiId)).thenReturn(Optional.of(domiLibre));
        Mockito.when(domiciliarioRepo.save(any(Domiciliario.class))).thenReturn(domiOcupado);

        // Act
        Domiciliario resultado = domiciliarioService.actualizarDisponibilidad(domiId, false);

        // Assert
        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.getDisponible()).isFalse();
        verify(domiciliarioRepo).save(any(Domiciliario.class));
    }

    /**
     * Prueba 4 [Regla de Negocio en actualizar() - Caso Conflicto/Fallo]: Valida que modificar los datos de un domiciliario e intentar
     * activar su disponibilidad lance la excepción (retorna conflicto) si tiene pedidos en estado "Enviado".
     */
    @Test
    public void domiciliarioService_actualizar_returnsConflict() {
        // Arrange
        Long domiId = 1L;
        Domiciliario domiActual = Domiciliario.builder()
                .id(domiId)
                .nombre("Carlos Gómez")
                .cedula("1010203040")
                .disponible(false)
                .build();

        Domiciliario datosModificados = Domiciliario.builder()
                .nombre("Carlos Gómez Editado")
                .disponible(true)
                .build();

        Pedido pedidoEnviado = Pedido.builder()
                .id(200L)
                .estado("En camino")
                .domiciliario(domiActual)
                .build();

        Mockito.when(domiciliarioRepo.findById(domiId)).thenReturn(Optional.of(domiActual));
        Mockito.when(pedidoRepo.findByDomiciliarioId(domiId)).thenReturn(List.of(pedidoEnviado));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> domiciliarioService.actualizar(domiId, datosModificados))
                .isInstanceOf(DomiciliarioConPedidoEnviadoException.class)
                .hasMessageContaining("No se puede marcar como libre/disponible al domiciliario")
                .hasMessageContaining("Enviado (en ruta de despacho)");

        verify(domiciliarioRepo, never()).save(any());
    }

    /**
     * Prueba 5 [Regla de Negocio en actualizar() - Caso Éxito]: Modificar datos y activar disponibilidad
     * cuando no tiene pedidos en ruta, retornando la entidad Domiciliario actualizada.
     */
    @Test
    public void domiciliarioService_actualizar_Domiciliario() {
        // Arrange
        Long domiId = 4L;
        Domiciliario domiActual = Domiciliario.builder()
                .id(domiId)
                .nombre("Javier Morales")
                .cedula("1040506070")
                .celular("+57 318 888 9999")
                .disponible(false)
                .build();

        Domiciliario datosModificados = Domiciliario.builder()
                .nombre("Javier Morales Actualizado")
                .celular("+57 318 999 0000")
                .disponible(true)
                .build();

        Domiciliario domiGuardado = Domiciliario.builder()
                .id(domiId)
                .nombre("Javier Morales Actualizado")
                .cedula("1040506070")
                .celular("+57 318 999 0000")
                .disponible(true)
                .build();

        Mockito.when(domiciliarioRepo.findById(domiId)).thenReturn(Optional.of(domiActual));
        Mockito.when(pedidoRepo.findByDomiciliarioId(domiId)).thenReturn(List.of());
        Mockito.when(domiciliarioRepo.save(any(Domiciliario.class))).thenReturn(domiGuardado);

        // Act
        Domiciliario resultado = domiciliarioService.actualizar(domiId, datosModificados);

        // Assert
        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.getNombre()).isEqualTo("Javier Morales Actualizado");
        Assertions.assertThat(resultado.getDisponible()).isTrue();
        verify(domiciliarioRepo).save(any(Domiciliario.class));
    }
}
