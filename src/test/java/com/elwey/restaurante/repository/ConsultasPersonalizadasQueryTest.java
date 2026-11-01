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
 * Incluye pruebas de lógica tanto positivas como de frontera/negativas para cada consulta.
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
     * Valida la concordancia exacta de ambos predicados (AND) y la exclusión de otros estados.
     */
    @Test
    public void testQuery_findPedidosPorEstadoYDomiciliario_retornaPedidosFiltrados() {
        // Caso Positivo: Carlos Gómez con pedido "Enviado"
        List<Pedido> resultado = pedidoRepository.findPedidosPorEstadoYDomiciliario("Enviado", domiciliarioCarlos.getId());

        Assertions.assertThat(resultado).isNotNull().hasSize(1);
        Assertions.assertThat(resultado.get(0).getId()).isEqualTo(pedido1.getId());
        Assertions.assertThat(resultado.get(0).getEstado()).isEqualTo("Enviado");
        Assertions.assertThat(resultado.get(0).getDomiciliario().getNombre()).isEqualTo("Carlos Gómez");

        // Casos Negativos: Carlos no tiene pedidos "Entregado", y un domiciliario inexistente no tiene pedidos
        List<Pedido> pedidosEntregados = pedidoRepository.findPedidosPorEstadoYDomiciliario("Entregado", domiciliarioCarlos.getId());
        Assertions.assertThat(pedidosEntregados).isEmpty();

        List<Pedido> pedidosDomiInexistente = pedidoRepository.findPedidosPorEstadoYDomiciliario("Enviado", 99999L);
        Assertions.assertThat(pedidosDomiInexistente).isEmpty();
    }

    /**
     * Consulta 2: @Query para buscar pedidos por el email del cliente (con JOIN y LOWER).
     * Valida que no distinga entre mayúsculas y minúsculas y que no devuelva pedidos ajenos.
     */
    @Test
    public void testQuery_findPedidosPorClienteEmail_retornaPedidosDelCliente() {
        // Caso Positivo: Búsqueda con email en mayúsculas
        List<Pedido> resultado = pedidoRepository.findPedidosPorClienteEmail("JUAN.PEREZ@ELWEY.COM");

        Assertions.assertThat(resultado).isNotNull().hasSize(2);
        Assertions.assertThat(resultado).extracting(p -> p.getCliente().getEmail())
                .containsOnly("juan.perez@elwey.com");

        // Caso Negativo: Email no registrado en el sistema
        List<Pedido> resultadoVacio = pedidoRepository.findPedidosPorClienteEmail("cliente.fantasma@elwey.com");
        Assertions.assertThat(resultadoVacio).isEmpty();
    }

    /**
     * Consulta 3: @Query con función de agregación COUNT para conteo de pedidos por estado.
     * Valida que la suma de estados coincida exactamente con la población de pedidos en base de datos.
     */
    @Test
    public void testQuery_countPedidosPorEstado_retornaConteoCorrecto() {
        // Act
        Long cocinando = pedidoRepository.countPedidosPorEstado("Cocinando");
        Long enviado = pedidoRepository.countPedidosPorEstado("Enviado");
        Long entregado = pedidoRepository.countPedidosPorEstado("Entregado");
        Long cancelado = pedidoRepository.countPedidosPorEstado("Cancelado");

        // Assert
        Assertions.assertThat(cocinando).isEqualTo(1L);
        Assertions.assertThat(enviado).isEqualTo(1L);
        Assertions.assertThat(entregado).isEqualTo(0L);
        Assertions.assertThat(cancelado).isEqualTo(0L);
    }

    /**
     * Consulta 4: @Query para filtrar comidas activas con precio mayor o igual al parámetro.
     * Valida tanto la cota inferior de precio como la exclusión estricta de platos inactivos (soft delete).
     */
    @Test
    public void testQuery_findComidasConPrecioMayorOIgual_retornaComidasActivasFiltradas() {
        // Act 1: Con umbral de 30000.0 -> solo Suadero (31500) cumple
        List<Comida> comidasCaras = comidaRepository.findComidasConPrecioMayorOIgual(30000.0);

        Assertions.assertThat(comidasCaras).isNotNull().hasSize(1);
        Assertions.assertThat(comidasCaras.get(0).getNombre()).isEqualTo("Tacos de Suadero");
        Assertions.assertThat(comidasCaras.get(0).getActivo()).isTrue();

        // Act 2: Con umbral de 20000.0 -> Pastor (28900) y Suadero (31500) cumplen
        // Tacos Descontinuados (35000) tiene precio suficiente, pero DEBE excluirse por activo = false
        List<Comida> todasActivas = comidaRepository.findComidasConPrecioMayorOIgual(20000.0);
        Assertions.assertThat(todasActivas).hasSize(2);
        Assertions.assertThat(todasActivas).extracting(Comida::getNombre)
                .containsExactlyInAnyOrder("Tacos al Pastor", "Tacos de Suadero");
        Assertions.assertThat(todasActivas).extracting(Comida::getActivo)
                .containsOnly(true);
    }

    /**
     * Consulta 5: @Query con filtro compuesto por nombre de categoría y etiqueta gastronómica.
     * Valida filtrado relacional sensible al contexto e insensible a mayúsculas/minúsculas.
     */
    @Test
    public void testQuery_findComidasPorCategoriaYEtiqueta_retornaComidaExacta() {
        // Caso Positivo: Categoría "tacos" y etiqueta "estrella" en minúsculas
        List<Comida> resultado = comidaRepository.findComidasPorCategoriaYEtiqueta("tacos", "estrella");

        Assertions.assertThat(resultado).isNotNull().hasSize(1);
        Assertions.assertThat(resultado.get(0).getNombre()).isEqualTo("Tacos al Pastor");
        Assertions.assertThat(resultado.get(0).getEtiqueta()).isEqualTo("Estrella");

        // Caso Negativo: Categoría existente con etiqueta inexistente
        List<Comida> resultadoInexistente = comidaRepository.findComidasPorCategoriaYEtiqueta("tacos", "Vegano");
        Assertions.assertThat(resultadoInexistente).isEmpty();

        // Caso Negativo: Categoría inexistente
        List<Comida> categoriaInexistente = comidaRepository.findComidasPorCategoriaYEtiqueta("Bebidas", "Estrella");
        Assertions.assertThat(categoriaInexistente).isEmpty();
    }
}
