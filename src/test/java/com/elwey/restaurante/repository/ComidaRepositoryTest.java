package com.elwey.restaurante.repository;

import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.entities.Comida;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

/**
 * Pruebas unitarias para el CRUD de un Repositorio (Semana 13).
 * Sigue el patrón Arrange - Act - Assert con @DataJpaTest y AssertJ.
 */
@DataJpaTest
@ActiveProfiles("test")
public class ComidaRepositoryTest {

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaTacos;
    private Comida comidaInicial;

    @BeforeEach
    public void setUp() {
        categoriaTacos = categoriaRepository.save(
                Categoria.builder()
                        .nombre("Tacos")
                        .descripcion("Auténticos tacos mexicanos")
                        .activo(true)
                        .build()
        );

        comidaInicial = comidaRepository.save(
                Comida.builder()
                        .nombre("Tacos al Pastor")
                        .precio(28900.0)
                        .descripcion("Carne de cerdo en achiote con piña asada")
                        .imagenUrl("/images/comidas/tacos-al-pastor.jpg")
                        .etiqueta("Estrella")
                        .activo(true)
                        .categoria(categoriaTacos)
                        .build()
        );
    }

    @Test
    public void comidaRepository_save_retornaComidaGuardada() {
        // Arrange
        Comida nuevaComida = Comida.builder()
                .nombre("Tacos de Suadero")
                .precio(31500.0)
                .descripcion("Corte suave de res confitado estilo CDMX")
                .imagenUrl("/images/comidas/tacos-de-suadero.jpg")
                .etiqueta("Tradicional")
                .activo(true)
                .categoria(categoriaTacos)
                .build();

        // Act
        Comida guardada = comidaRepository.save(nuevaComida);

        // Assert
        Assertions.assertThat(guardada).isNotNull();
        Assertions.assertThat(guardada.getId()).isNotNull();
        Assertions.assertThat(guardada.getNombre()).isEqualTo("Tacos de Suadero");
    }

    @Test
    public void comidaRepository_findAll_retornaListaNoVacia() {
        // Act
        List<Comida> comidas = comidaRepository.findAll();

        // Assert
        Assertions.assertThat(comidas).isNotNull();
        Assertions.assertThat(comidas.size()).isGreaterThan(0);
        Assertions.assertThat(comidas).contains(comidaInicial);
    }

    @Test
    public void comidaRepository_findById_retornaVacioCuandoNoExiste() {
        // Act
        Optional<Comida> comida = comidaRepository.findById(-1L);

        // Assert
        Assertions.assertThat(comida).isEmpty();
    }

    @Test
    public void comidaRepository_update_modificaComida() {
        // Arrange
        Comida comidaExistente = comidaRepository.findById(comidaInicial.getId()).orElse(null);
        Assertions.assertThat(comidaExistente).isNotNull();
        comidaExistente.setNombre("Tacos al Pastor Especiales");
        comidaExistente.setPrecio(32000.0);

        // Act
        Comida comidaActualizada = comidaRepository.save(comidaExistente);

        // Assert
        Assertions.assertThat(comidaActualizada).isNotNull();
        Assertions.assertThat(comidaActualizada.getNombre()).isEqualTo("Tacos al Pastor Especiales");
        Assertions.assertThat(comidaActualizada.getPrecio()).isEqualTo(32000.0);
    }

    @Test
    public void comidaRepository_delete_eliminaComida() {
        // Arrange
        Long id = comidaInicial.getId();

        // Act
        comidaRepository.deleteById(id);
        Optional<Comida> comidaEliminada = comidaRepository.findById(id);

        // Assert
        Assertions.assertThat(comidaEliminada).isEmpty();
    }
}
