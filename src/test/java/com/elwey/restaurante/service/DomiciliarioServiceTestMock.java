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
 * - Nomenclatura <metodo>_<comportamiento>
 * - Aislamiento estricto de repositorios mediante simulación Mockito.
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
     * Regla de negocio: Si se intenta poner libre (disponible = true) a un domiciliario
     * que se encuentra en un pedido "Enviado", el servicio DEBE lanzar DomiciliarioConPedidoEnviadoException
     * y NO guardar cambios en la base de datos.
     */
    @Test
    public void actualizarDisponibilidad_throwsDomiciliarioConPedidoEnviadoException_whenTienePedidoEnviado() {
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
     * Regla de negocio: Si el domiciliario no tiene pedidos en estado "Enviado" (por ejemplo, pedidos Entregados
     * o lista vacía), se permite marcarlo como libre / disponible exitosamente.
     */
    @Test
    public void actualizarDisponibilidad_returnsDomiciliario_whenNoTienePedidosEnviados() {
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
     * Regla de negocio: Marcar como ocupado (disponible = false) a un domiciliario libre
     * siempre es una operación permitida sin restricciones de pedidos en ruta.
     */
    @Test
    public void actualizarDisponibilidad_permiteMarcarOcupado_inclusoConPedidoEnviado() {
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
     * Regla de negocio en actualizar(): Valida que modificar los datos de un domiciliario e intentar
     * activar su disponibilidad lance la excepción si tiene pedidos en estado "Enviado".
     */
    @Test
    public void actualizar_throwsDomiciliarioConPedidoEnviadoException_whenIntentaPonerLibreConPedidoEnviado() {
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
}
