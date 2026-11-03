package com.elwey.restaurante.repository;

import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.entities.Comida;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

/**
 * Pruebas unitarias para el CRUD de ComidaRepository (Semana 13).
 * Sigue la doctrina del docente Juan Sebastián Angarita:
 * - JUnit 5 Jupiter puro con @DataJpaTest y @BeforeEach
 * - Patrón Arrange - Act - Assert con AssertJ
 * - Nomenclatura doctrinal: [componente]_[metodo]_[lo_que_retorna]
 */
@DataJpaTest
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

    /**
     * Prueba 1 [CREATE]: Guardar una nueva entidad Comida.
     * Retorna el objeto Comida persistido con su ID asignado.
     */
    @Test
    public void comidaRepository_save_Comida() {
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
        Assertions.assertThat(guardada.getId()).isNotNull().isPositive();
        Assertions.assertThat(guardada.getNombre()).isEqualTo("Tacos de Suadero");
        Assertions.assertThat(guardada.getPrecio()).isEqualTo(31500.0);
        Assertions.assertThat(guardada.getActivo()).isTrue();
        Assertions.assertThat(guardada.getCategoria()).isNotNull();
        Assertions.assertThat(guardada.getCategoria().getId()).isEqualTo(categoriaTacos.getId());
    }

    /**
     * Prueba 2 [READ ALL]: Consultar todas las entidades activas en el repositorio.
     * Retorna una lista (returnsList) de comidas.
     */
    @Test
    public void comidaRepository_findAll_returnsList() {
        // Act
        List<Comida> comidas = comidaRepository.findAll();

        // Assert
        Assertions.assertThat(comidas).isNotNull().isNotEmpty();
        Assertions.assertThat(comidas.size()).isGreaterThanOrEqualTo(1);
        Assertions.assertThat(comidas).extracting(Comida::getNombre).contains("Tacos al Pastor");
        Assertions.assertThat(comidas).extracting(c -> c.getCategoria().getNombre()).contains("Tacos");
    }

    /**
     * Prueba 3 [READ BY ID - Caso Positivo]: Buscar una entidad existente por su ID.
     * Retorna el objeto Comida envuelto en Optional.
     */
    @Test
    public void comidaRepository_findById_Comida() {
        // Act
        Optional<Comida> comida = comidaRepository.findById(comidaInicial.getId());

        // Assert
        Assertions.assertThat(comida).isPresent();
        Assertions.assertThat(comida.get().getId()).isEqualTo(comidaInicial.getId());
        Assertions.assertThat(comida.get().getNombre()).isEqualTo("Tacos al Pastor");
    }

    /**
     * Prueba 4 [READ BY ID - Caso Negativo]: Buscar una entidad inexistente.
     * Retorna NotFound / vacío (returnsNotFound).
     */
    @Test
    public void comidaRepository_findById_returnsNotFound() {
        // Act
        Optional<Comida> comida = comidaRepository.findById(-1L);
        Optional<Comida> comidaIdInexistente = comidaRepository.findById(99999L);

        // Assert
        Assertions.assertThat(comida).isEmpty();
        Assertions.assertThat(comidaIdInexistente).isEmpty();
    }

    /**
     * Prueba 5 [UPDATE]: Modificar campos de una entidad existente y persistir cambios.
     * Retorna el objeto Comida modificado.
     */
    @Test
    public void comidaRepository_update_Comida() {
        // Arrange
        Comida comidaExistente = comidaRepository.findById(comidaInicial.getId()).orElse(null);
        Assertions.assertThat(comidaExistente).isNotNull();
        comidaExistente.setNombre("Tacos al Pastor Especiales");
        comidaExistente.setPrecio(32000.0);
        comidaExistente.setEtiqueta("Edición Limitada");

        // Act
        Comida comidaActualizada = comidaRepository.save(comidaExistente);

        // Assert
        Assertions.assertThat(comidaActualizada).isNotNull();
        Assertions.assertThat(comidaActualizada.getId()).isEqualTo(comidaInicial.getId());
        Assertions.assertThat(comidaActualizada.getNombre()).isEqualTo("Tacos al Pastor Especiales");
        Assertions.assertThat(comidaActualizada.getPrecio()).isEqualTo(32000.0);
        Assertions.assertThat(comidaActualizada.getEtiqueta()).isEqualTo("Edición Limitada");

        // Verificación de recarga desde BD
        Comida recargada = comidaRepository.findById(comidaInicial.getId()).orElse(null);
        Assertions.assertThat(recargada).isNotNull();
        Assertions.assertThat(recargada.getNombre()).isEqualTo("Tacos al Pastor Especiales");
        Assertions.assertThat(recargada.getPrecio()).isEqualTo(32000.0);
    }

    /**
     * Prueba 6 [DELETE]: Eliminar una entidad por su identificador.
     * Retorna vacío (returnsEmpty) / void tras eliminar del repositorio.
     */
    @Test
    public void comidaRepository_delete_returnsEmpty() {
        // Arrange
        Long id = comidaInicial.getId();
        long totalAntes = comidaRepository.count();

        // Act
        comidaRepository.deleteById(id);
        Optional<Comida> comidaEliminada = comidaRepository.findById(id);
        long totalDespues = comidaRepository.count();

        // Assert
        Assertions.assertThat(comidaEliminada).isEmpty();
        Assertions.assertThat(totalDespues).isEqualTo(totalAntes - 1L);
    }
}
