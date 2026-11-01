package com.elwey.restaurante.repository;

import com.elwey.restaurante.entities.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pruebas unitarias para las 5 consultas personalizadas creadas con @Query (Semana 13).
 * Valida consultas SQL/JPQL escritas a mano con parámetros, filtros, joins y funciones de agregación.
 */
@DataJpaTest
@ActiveProfiles("test")
public class ConsultasPersonalizadasQueryTest {

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    private Categoria categoriaTacos;
    private Cliente clienteJuan;
    private Domiciliario domiciliarioCarlos;
    private Comida comidaPastor;
    private Comida comidaSuadero;
    private Pedido pedido1;
    private Pedido pedido2;

    @BeforeEach
    public void setUp() {
        categoriaTacos = categoriaRepository.save(
                Categoria.builder()
                        .nombre("Tacos")
                        .descripcion("Auténticos tacos mexicanos")
                        .activo(true)
                        .build()
        );

        comidaPastor = comidaRepository.save(
                Comida.builder()
                        .nombre("Tacos al Pastor")
                        .precio(28900.0)
                        .descripcion("Cerdo en achiote con piña")
                        .etiqueta("Estrella")
                        .activo(true)
                        .categoria(categoriaTacos)
                        .build()
        );

        comidaSuadero = comidaRepository.save(
                Comida.builder()
                        .nombre("Tacos de Suadero")
                        .precio(31500.0)
                        .descripcion("Corte suave de res confitado")
                        .etiqueta("Tradicional")
                        .activo(true)
                        .categoria(categoriaTacos)
                        .build()
        );

        // Comida inactiva para probar el filtro de activo=true
        comidaRepository.save(
                Comida.builder()
                        .nombre("Tacos Descontinuados")
                        .precio(35000.0)
                        .descripcion("Plato inactivo")
                        .etiqueta("Tradicional")
                        .activo(false)
                        .categoria(categoriaTacos)
                        .build()
        );

        clienteJuan = clienteRepository.save(
                Cliente.builder()
                        .nombre("Juan")
                        .apellido("Pérez")
                        .email("juan.perez@elwey.com")
                        .password("pass123")
                        .telefono("3001234567")
                        .direccion("Calle 100 #15-20")
                        .activo(true)
                        .build()
        );

        domiciliarioCarlos = domiciliarioRepository.save(
                Domiciliario.builder()
                        .nombre("Carlos Gómez")
                        .cedula("1010203040")
                        .celular("+57 311 222 3333")
                        .contrasena("domi123")
                        .disponible(true)
                        .build()
        );

        // Pedido 1: asignado a Carlos Gómez y en ruta (Enviado)
        pedido1 = pedidoRepository.save(
                Pedido.builder()
                        .cliente(clienteJuan)
                        .domiciliario(domiciliarioCarlos)
                        .estado("Enviado")
                        .fechaCreacion(LocalDateTime.now().minusMinutes(20))
                        .items(new ArrayList<>())
                        .build()
        );

        // Pedido 2: sin domiciliario asignado y en cocina (Cocinando)
        pedido2 = pedidoRepository.save(
                Pedido.builder()
                        .cliente(clienteJuan)
                        .domiciliario(null)
                        .estado("Cocinando")
                        .fechaCreacion(LocalDateTime.now().minusMinutes(10))
                        .items(new ArrayList<>())
                        .build()
        );
    }

    /**
     * Consulta 1: @Query para buscar pedidos por estado y domiciliario asignado.
     */
    @Test
    public void testQuery_findPedidosPorEstadoYDomiciliario_retornaPedidosFiltrados() {
        // Act
        List<Pedido> resultado = pedidoRepository.findPedidosPorEstadoYDomiciliario("Enviado", domiciliarioCarlos.getId());

        // Assert
        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.size()).isEqualTo(1);
        Assertions.assertThat(resultado.get(0).getId()).isEqualTo(pedido1.getId());
        Assertions.assertThat(resultado.get(0).getDomiciliario().getNombre()).isEqualTo("Carlos Gómez");
    }

    /**
     * Consulta 2: @Query para buscar pedidos por el email del cliente (con JOIN implícito y LOWER).
     */
    @Test
    public void testQuery_findPedidosPorClienteEmail_retornaPedidosDelCliente() {
        // Act
        List<Pedido> resultado = pedidoRepository.findPedidosPorClienteEmail("JUAN.PEREZ@ELWEY.COM");

        // Assert
        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.size()).isEqualTo(2);
        Assertions.assertThat(resultado).extracting(p -> p.getCliente().getEmail())
                .containsOnly("juan.perez@elwey.com");
    }

    /**
     * Consulta 3: @Query con función de agregación COUNT para conteo de pedidos por estado.
     */
    @Test
    public void testQuery_countPedidosPorEstado_retornaConteoCorrecto() {
        // Act
        Long cocinando = pedidoRepository.countPedidosPorEstado("Cocinando");
        Long enviado = pedidoRepository.countPedidosPorEstado("Enviado");
        Long entregado = pedidoRepository.countPedidosPorEstado("Entregado");

        // Assert
        Assertions.assertThat(cocinando).isEqualTo(1L);
        Assertions.assertThat(enviado).isEqualTo(1L);
        Assertions.assertThat(entregado).isEqualTo(0L);
    }

    /**
     * Consulta 4: @Query para filtrar comidas activas con precio mayor o igual al parámetro.
     */
    @Test
    public void testQuery_findComidasConPrecioMayorOIgual_retornaComidasActivasFiltradas() {
        // Act
        List<Comida> comidasCaras = comidaRepository.findComidasConPrecioMayorOIgual(30000.0);

        // Assert
        // Solo "Tacos de Suadero" (31500) cumple: "Tacos al Pastor" cuesta 28900 (< 30000)
        // y "Tacos Descontinuados" cuesta 35000 pero tiene activo = false
        Assertions.assertThat(comidasCaras).isNotNull();
        Assertions.assertThat(comidasCaras.size()).isEqualTo(1);
        Assertions.assertThat(comidasCaras.get(0).getNombre()).isEqualTo("Tacos de Suadero");
    }

    /**
     * Consulta 5: @Query con filtro compuesto por nombre de categoría y etiqueta oficial.
     */
    @Test
    public void testQuery_findComidasPorCategoriaYEtiqueta_retornaComidaExacta() {
        // Act
        List<Comida> resultado = comidaRepository.findComidasPorCategoriaYEtiqueta("tacos", "estrella");

        // Assert
        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.size()).isEqualTo(1);
        Assertions.assertThat(resultado.get(0).getNombre()).isEqualTo("Tacos al Pastor");
        Assertions.assertThat(resultado.get(0).getEtiqueta()).isEqualTo("Estrella");
    }
}
