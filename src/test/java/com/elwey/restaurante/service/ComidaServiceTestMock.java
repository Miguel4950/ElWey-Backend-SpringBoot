package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.repository.CategoriaRepository;
import com.elwey.restaurante.repository.ComidaRepository;
import com.elwey.restaurante.repository.ItemPedidoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * Pruebas unitarias de Servicio con Mocks para ComidaService.
 * Sigue la doctrina del profesor Angarita:
 * - @ExtendWith(MockitoExtension.class)
 * - @ActiveProfiles("test")
 * - Patrón Arrange - Act - Assert con AssertJ
 * - Nomenclatura [componente]_[metodo]_[lo_que_retorna]
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ComidaServiceTestMock {

    @Mock
    private ComidaRepository comidaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @InjectMocks
    private ComidaServiceImpl comidaService;

    /**
     * Prueba [READ - Query]: Obtener las etiquetas activas únicas.
     * Retorna una lista con las etiquetas ordenadas alfabéticamente.
     */
    @Test
    public void comidaService_listarEtiquetasActivas_returnsList() {
        // Arrange
        List<String> mockEtiquetas = List.of("Bebidas", "Estrella", "Picante", "Tradicional");
        Mockito.when(comidaRepository.findDistinctEtiquetasActivas()).thenReturn(mockEtiquetas);

        // Act
        List<String> resultado = comidaService.listarEtiquetasActivas();

        // Assert
        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado).hasSize(4);
        Assertions.assertThat(resultado).containsExactly("Bebidas", "Estrella", "Picante", "Tradicional");
        Mockito.verify(comidaRepository).findDistinctEtiquetasActivas();
    }

    /**
     * Prueba [READ]: Listar comidas activas para el menú.
     * Retorna la lista de comidas con activo = true.
     */
    @Test
    public void comidaService_listarActivas_returnsList() {
        // Arrange
        Comida c1 = Comida.builder().id(1L).nombre("Taco").activo(true).build();
        Comida c2 = Comida.builder().id(2L).nombre("Burrito").activo(true).build();
        Mockito.when(comidaRepository.findByActivoTrue()).thenReturn(List.of(c1, c2));

        // Act
        List<Comida> resultado = comidaService.listarActivas();

        // Assert
        Assertions.assertThat(resultado).isNotNull().hasSize(2);
        Assertions.assertThat(resultado).extracting(Comida::getNombre).containsExactly("Taco", "Burrito");
        Mockito.verify(comidaRepository).findByActivoTrue();
    }
}
