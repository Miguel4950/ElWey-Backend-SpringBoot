package com.elwey.restaurante;

import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.entities.Administrador;
import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.entities.Cliente;
import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.entities.Domiciliario;
import com.elwey.restaurante.entities.ItemPedido;
import com.elwey.restaurante.entities.ItemPedidoAdicional;
import com.elwey.restaurante.entities.Operador;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.repository.AdicionalRepository;
import com.elwey.restaurante.repository.AdministradorRepository;
import com.elwey.restaurante.repository.CategoriaRepository;
import com.elwey.restaurante.repository.ClienteRepository;
import com.elwey.restaurante.repository.ComidaRepository;
import com.elwey.restaurante.repository.DomiciliarioRepository;
import com.elwey.restaurante.repository.ItemPedidoAdicionalRepository;
import com.elwey.restaurante.repository.ItemPedidoRepository;
import com.elwey.restaurante.repository.OperadorRepository;
import com.elwey.restaurante.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class DataLoader implements CommandLineRunner {

        @Autowired
        private CategoriaRepository categoriaRepository;

        @Autowired
        private ComidaRepository comidaRepository;

        @Autowired
        private ClienteRepository clienteRepository;

        @Autowired
        private AdicionalRepository adicionalRepository;

        @Autowired
        private DomiciliarioRepository domiciliarioRepository;

        @Autowired
        private PedidoRepository pedidoRepository;

        @Autowired
        private ItemPedidoRepository itemPedidoRepository;

        @Autowired
        private ItemPedidoAdicionalRepository itemPedidoAdicionalRepository;

        @Autowired
        private AdministradorRepository administradorRepository;

        @Autowired
        private OperadorRepository operadorRepository;

        @Override
        public void run(String... args) throws Exception {

                // 1. Crear 5 Categorías representativas del restaurante
                String[] nombresCategorias = { "Tacos", "Quesadillas", "Entradas", "Especialidades", "Burritos", "Bebidas" };
                String[] descCategorias = {
                                "Variedad de tacos tradicionales servidos en tortilla de maíz o harina con ingredientes frescos.",
                                "Muestras de queso fundido e ingredientes variados en tortillas a la plancha.",
                                "Platos ideales para comenzar la experiencia o compartir en la mesa.",
                                "Platos insignia del restaurante preparados con recetas exclusivas de la casa.",
                                "Tortillas de harina gigantes rellenas de carne, frijoles, arroz y complementos.",
                                "Gaseosas frías, aguas frescas tradicionales y cervezas para acompañar tu orden."
                };

                List<Categoria> categorias = new ArrayList<>();
                for (int i = 0; i < nombresCategorias.length; i++) {
                        Categoria c = Categoria.builder()
                                        .nombre(nombresCategorias[i])
                                        .descripcion(descCategorias[i])
                                        .activo(true)
                                        .build();
                        categorias.add(categoriaRepository.save(c));
                }

                Categoria catTacos = categorias.get(0);
                Categoria catQuesadillas = categorias.get(1);
                Categoria catEntradas = categorias.get(2);
                Categoria catEspecialidades = categorias.get(3);
                Categoria catBurritos = categorias.get(4);
                Categoria catBebidas = categorias.get(5);

                // 2. Crear 40 Comidas auténticas (los 8 originales del Mockup + 32 platos
                // adicionales)
                List<Comida> comidas = new ArrayList<>();

                // Los 8 platos emblemáticos del Sprint 1 y 2
                comidas.add(new Comida(null, "Tacos al Pastor", 28900.0,
                                "Carne de cerdo marinada en achiote tradicional, piña asada al carbón, cilantro fresco y cebolla en tortilla de maíz nixtamalizada.",
                                "/images/comidas/tacos-al-pastor.jpg",
                                "Estrella", true, catTacos));

                comidas.add(new Comida(null, "Quesadilla de Birria", 34800.0,
                                "Tortilla de maíz rellena de queso Oaxaca fundido y jugosa birria de res cocinada a fuego lento por 8 horas. Acompañada de consomé para sumergir.",
                                "/images/comidas/quesadilla-de-birria.jpg",
                                "Estrella", true, catQuesadillas));

                comidas.add(new Comida(null, "Guacamole El Wey", 24500.0,
                                "Aguacate Hass seleccionado machacado al momento en molcajete con tomate, jalapeño, cilantro y toque de limón. Servido con totopos artesanales crujientes.",
                                "/images/comidas/guacamole-el-wey.jpg",
                                "Vegetariano", true, catEntradas));

                comidas.add(new Comida(null, "Enchiladas Rojas", 33900.0,
                                "Tres tortillas de maíz rellenas de pechuga de pollo deshebrada, bañadas en salsa roja de chiles secos, cubiertas con crema ácida, queso fresco y cebolla morada.",
                                "/images/comidas/enchiladas-rojas.jpg",
                                "Estrella", true, catEspecialidades));

                comidas.add(new Comida(null, "Burrito Norteño", 36900.0,
                                "Tortilla de harina gigante rellena de carne asada al carbón, frijoles refritos bayos, arroz a la mexicana, queso Chihuahua fundido y pico de gallo.",
                                "/images/comidas/burrito-norteno.jpg",
                                "Tradicional", true, catBurritos));

                comidas.add(new Comida(null, "Tacos de Suadero", 31500.0,
                                "Corte suave de res confitado lentamente en su propia grasa estilo CDMX, picado al momento con cilantro criollo, cebolla y salsa taquera verde.",
                                "/images/comidas/tacos-de-suadero.jpg",
                                "Tradicional", true, catTacos));

                comidas.add(new Comida(null, "Nachos El Wey", 39900.0,
                                "Cama abundante de totopos de maíz crujientes, frijoles refritos, abundante queso fundido Oaxaca y Cheddar, pico de gallo fresco, jalapeños en escabeche, crema ácida y guacamole especial.",
                                "/images/comidas/nachos-el-wey.jpg",
                                "Para Compartir", true, catEntradas));

                comidas.add(new Comida(null, "Gringa con Carne (la favorita de jaimes)", 32800.0,
                                "Deliciosa quesadilla en tortilla de harina dorada a la plancha con mantequilla, rellena de abundante carne al pastor, queso Oaxaca derretido, piña asada caramelizada y cilantro fresco.",
                                "/images/comidas/gringa-con-carne.jpg",
                                "Estrella", true, catQuesadillas));

                // Platos 9 al 40 (32 platillos mexicanos adicionales)
                String[] nombresExtra = {
                                "Tacos de Carnitas Michoacán", "Tacos de Pescado Baja", "Tacos Gobernador",
                                "Tacos de Cochinita Pibil",
                                "Tacos de Barbacoa Hidalguense", "Tacos de Rib Eye con Tuétano",
                                "Tacos de Chicharrón en Salsa Verde", "Tacos de Asada Poblana",
                                "Quesadilla de Flor de Calabaza", "Quesadilla de Champiñones al Ajillo",
                                "Quesadilla Sincronizada", "Quesadilla de Chicharrón Prensado",
                                "Quesadilla Norteña con Arrachera", "Quesadilla de Tinga Poblana",
                                "Totopos con Queso Fundido y Rajas", "Esquites Callejeros con Epazote",
                                "Queso Fundido con Chorizo Casero", "Flautas Doradas de Pollo",
                                "Sopes Tradicionales con Cecina", "Tostadas de Tinga con Crema",
                                "Chiles en Nogada de Puebla", "Pozole Rojo Tradicional", "Mole Poblano con Pollo",
                                "Tamal Oaxaqueño en Hoja de Plátano",
                                "Fajitas Mixtas al Carbón", "Cochinita Pibil en Plato Hondo",
                                "Birria Tapatía de Res en Cazuela", "Alambre Mixto de Res y Tocino",
                                "Burrito California con Papas", "Burrito de Tinga Deshebrada",
                                "Burrito al Pastor con Queso", "Burrito Vegetariano con Frijol Negro"
                };

                Double[] preciosExtra = {
                                29900.0, 35900.0, 38900.0, 31900.0,
                                34500.0, 44900.0, 27900.0, 32500.0,
                                26900.0, 27500.0, 28900.0, 29800.0,
                                37900.0, 30500.0, 23900.0, 19800.0,
                                31900.0, 28900.0, 29500.0, 27800.0,
                                48900.0, 39500.0, 41900.0, 22500.0,
                                45900.0, 39900.0, 42800.0, 43900.0,
                                38900.0, 33900.0, 35800.0, 31500.0
                };

                String[] fotosExtra = {
                                "/images/comidas/tacos-carnitas-michoacan.jpg",
                                "/images/comidas/tacos-pescado-baja.jpg",
                                "/images/comidas/tacos-gobernador.jpg",
                                "/images/comidas/tacos-cochinita-pibil.jpg",
                                "/images/comidas/tacos-barbacoa-hidalguense.jpg",
                                "/images/comidas/tacos-ribeye-tuetano.jpg",
                                "/images/comidas/tacos-chicharron-verde.jpg",
                                "/images/comidas/tacos-asada-poblana.jpg",
                                "/images/comidas/quesadilla-flor-calabaza.jpg",
                                "/images/comidas/quesadilla-champinones.jpg",
                                "/images/comidas/quesadilla-sincronizada.jpg",
                                "/images/comidas/quesadilla-chicharron-prensado.jpg",
                                "/images/comidas/quesadilla-nortena-arrachera.jpg",
                                "/images/comidas/quesadilla-tinga-poblana.jpg",
                                "/images/comidas/totopos-queso-fundido.jpg",
                                "/images/comidas/esquites-callejeros.jpg",
                                "/images/comidas/queso-fundido-chorizo.jpg",
                                "/images/comidas/flautas-doradas-pollo.jpg",
                                "/images/comidas/sopes-tradicionales-cecina.jpg",
                                "/images/comidas/tostadas-tinga-crema.jpg",
                                "/images/comidas/chiles-en-nogada.jpg",
                                "/images/comidas/pozole-rojo.jpg",
                                "/images/comidas/mole-poblano.jpg",
                                "/images/comidas/tamal-oaxaqueno.jpg",
                                "/images/comidas/fajitas-mixtas-carbon.jpg",
                                "/images/comidas/cochinita-pibil-plato.jpg",
                                "/images/comidas/birria-tapatia-cazuela.jpg",
                                "/images/comidas/alambre-mixto-res.jpg",
                                "/images/comidas/burrito-california-papas.jpg",
                                "/images/comidas/burrito-tinga-deshebrada.jpg",
                                "/images/comidas/burrito-pastor-queso.jpg",
                                "/images/comidas/burrito-vegetariano.jpg"
                };

                Categoria[] catAsignadaExtra = {
                                catTacos, catTacos, catTacos, catTacos,
                                catTacos, catTacos, catTacos, catTacos,
                                catQuesadillas, catQuesadillas, catQuesadillas, catQuesadillas,
                                catQuesadillas, catQuesadillas, catEntradas, catEntradas,
                                catEntradas, catEntradas, catEntradas, catEntradas,
                                catEspecialidades, catEspecialidades, catEspecialidades, catEspecialidades,
                                catEspecialidades, catEspecialidades, catEspecialidades, catEspecialidades,
                                catBurritos, catBurritos, catBurritos, catBurritos
                };

                String[] tagsExtra = {
                                "Tradicional", "Tradicional", "Estrella", "Tradicional",
                                "Tradicional", "Estrella", "Picante", "Tradicional",
                                "Vegetariano", "Vegetariano", "Tradicional", "Tradicional",
                                "Estrella", "Picante", "Para Compartir", "Vegetariano",
                                "Para Compartir", "Tradicional", "Tradicional", "Picante",
                                "Estrella", "Estrella", "Picante", "Tradicional",
                                "Estrella", "Tradicional", "Estrella", "Para Compartir",
                                "Tradicional", "Picante", "Estrella", "Vegetariano"
                };

                for (int i = 0; i < nombresExtra.length; i++) {
                        comidas.add(new Comida(null, nombresExtra[i], preciosExtra[i],
                                        "Auténtico plato de " + nombresExtra[i]
                                                        + " elaborado con ingredientes de la más alta calidad mexicana.",
                                        fotosExtra[i], tagsExtra[i], true, catAsignadaExtra[i]));
                }

                // Bebidas y Refrescos (Gaseosas populares y bebidas tradicionales mexicanas)
                String[] nombresBebidas = {
                                "Coca-Cola Original 400ml", "Coca-Cola Sin Azúcar 400ml",
                                "Sprite Lima-Limón 400ml", "Postobón Manzana 400ml",
                                "Colombiana La Nuestra 400ml", "Cuatro Toronja 400ml",
                                "Agua de Horchata Tradicional", "Agua de Jamaica Casera",
                                "Jarritos de Mandarina 370ml", "Jarritos de Tamarindo 370ml",
                                "Cerveza Corona Extra 355ml", "Cerveza Negra Modelo 355ml"
                };

                Double[] preciosBebidas = {
                                6800.0, 6800.0,
                                6800.0, 5900.0,
                                5900.0, 6800.0,
                                8500.0, 7900.0,
                                11500.0, 11500.0,
                                12900.0, 13500.0
                };

                String[] fotosBebidas = {
                                "/images/comidas/coca-cola.jpg", "/images/comidas/coca-cola-zero.jpg",
                                "/images/comidas/sprite.jpg", "/images/comidas/postobon-manzana.jpg",
                                "/images/comidas/colombiana.jpg", "/images/comidas/cuatro.jpg",
                                "/images/comidas/agua-horchata.jpg", "/images/comidas/agua-jamaica.jpg",
                                "/images/comidas/jarritos-mandarina.jpg", "/images/comidas/jarritos-tamarindo.jpg",
                                "/images/comidas/cerveza-corona.jpg", "/images/comidas/cerveza-modelo.jpg"
                };

                String[] descBebidas = {
                                "Gaseosa clásica servida bien fría con hielo y rodaja de limón.",
                                "Todo el sabor original y burbujeante de Coca-Cola pero sin calorías.",
                                "Gaseosa cristalina de sabor lima-limón 100% refrescante.",
                                "El clásico y dulce sabor a manzana consentido de Colombia.",
                                "Refresco sabor kola tradicional, el maridaje perfecto con comida mexicana.",
                                "Gaseosa cítrica de toronja natural, ideal para acompañar tacos picantes.",
                                "Bebida mexicana artesanal elaborada a base de arroz, leche, canela en rama y vainilla de Papantla.",
                                "Infusión fría de flor de jamaica natural con un toque cítrico y endulzada al punto justo.",
                                "Famoso refresco mexicano importado con sabor natural a mandarina jugosa.",
                                "Auténtico refresco mexicano con pulpa de tamarindo agridulce y toque cítrico.",
                                "Cerveza rubia tipo pilsner mexicana servida con su tradicional limón y sal marina.",
                                "Cerveza mexicana tipo Munich oscura con notas tostadas de caramelo y malta."
                };

                String[] tagsBebidas = {
                                "Tradicional", "Tradicional",
                                "Tradicional", "Tradicional",
                                "Tradicional", "Tradicional",
                                "Estrella", "Tradicional",
                                "Estrella", "Tradicional",
                                "Estrella", "Estrella"
                };

                for (int i = 0; i < nombresBebidas.length; i++) {
                        comidas.add(new Comida(null, nombresBebidas[i], preciosBebidas[i],
                                        descBebidas[i], fotosBebidas[i], tagsBebidas[i], true, catBebidas));
                }

                for (Comida c : comidas) {
                        comidaRepository.save(c);
                }

                // 3. Crear 10 Clientes (Jugadores de fútbol famosos con emails únicos)
                String[] nombresClientes = {
                                "Lionel", "Cristiano", "Radamel", "James", "Luis",
                                "Neymar", "Kylian", "Luka", "Erling", "Zinedine"
                };
                String[] apellidosClientes = {
                                "Messi", "Ronaldo", "Falcao", "Rodríguez", "Díaz",
                                "Jr", "Mbappé", "Modrić", "Haaland", "Zidane"
                };
                String[] emailsClientes = {
                                "lionel.messi@email.com", "cristiano.ronaldo@email.com", "falcao.garcia@email.com",
                                "james.rodriguez@email.com", "luis.diaz@email.com",
                                "neymar.jr@email.com", "kylian.mbappe@email.com", "luka.modric@email.com",
                                "erling.haaland@email.com", "zinedine.zidane@email.com"
                };
                String[] passwords = {
                                "messi10", "cr7cr7", "falcao9", "james10", "lucho7",
                                "ney11", "mbappe9", "modric10", "haaland9", "zizou5"
                };
                String[] telefonos = {
                                "+57 300 101 0101", "+57 311 777 0707", "+57 320 999 0909", "+57 301 101 1010",
                                "+57 315 707 0707",
                                "+57 318 111 1111", "+57 312 909 0909", "+57 310 101 0100", "+57 314 909 9090",
                                "+57 317 505 0505"
                };
                String[] direcciones = {
                                "Avenida Rosarina 10, Rosario", "Quinta da Madeira 7, Lisboa",
                                "Carrera 30 #57-60 El Campín, Bogotá", "Calle 10 #10-10, Envigado",
                                "Calle Principal #7, Barrancas Guajira",
                                "Avenida Santos 11, São Paulo", "Paseo de la Castellana 9, Madrid",
                                "Calle Zadar 10, Madrid", "Etihad Campus Way 9, Mánchester",
                                "Boulevard Marsella 5, Madrid"
                };

                for (int i = 0; i < 10; i++) {
                        Cliente cliente = Cliente.builder()
                                        .nombre(nombresClientes[i])
                                        .apellido(apellidosClientes[i])
                                        .email(emailsClientes[i])
                                        .password(passwords[i])
                                        .telefono(telefonos[i])
                                        .direccion(direcciones[i])
                                        .fechaRegistro("2026-0" + ((i % 3) + 1) + "-15")
                                        .build();
                        clienteRepository.save(cliente);
                }

                // 4. Crear adicionales distribuidos en las 5 Categorías
                List<Adicional> adicionales = new ArrayList<>();

                // Tacos extras
                adicionales.add(Adicional.builder().nombre("Cebollitas Cambray Asadas").precio(4900.0).activo(true)
                                .build());
                adicionales.add(Adicional.builder().nombre("Salsa Habanera Tatemada Extra").precio(3900.0).activo(true)
                                .build());
                adicionales.add(Adicional.builder().nombre("Porción Piña Asada Caramelizada").precio(3500.0)
                                .activo(true).build());

                // Quesadillas extras
                adicionales.add(Adicional.builder().nombre("Queso Oaxaca Extra Fundido").precio(6900.0).activo(true)
                                .build());
                adicionales.add(Adicional.builder().nombre("Guacamole Tradicional Extra").precio(6500.0).activo(true)
                                .build());
                adicionales.add(Adicional.builder().nombre("Chorizo Casero a la Plancha").precio(6900.0).activo(true)
                                .build());

                // Entradas extras
                adicionales.add(Adicional.builder().nombre("Totopos Artesanales Crujientes").precio(5500.0).activo(true)
                                .build());
                adicionales.add(Adicional.builder().nombre("Pico de Gallo Fresco Casero").precio(3900.0).activo(true)
                                .build());
                adicionales.add(Adicional.builder().nombre("Crema Ácida Mexicana Especial").precio(3900.0).activo(true)
                                .build());

                // Especialidades extras
                adicionales.add(Adicional.builder().nombre("Consomé de Birria para Sumergir").precio(7900.0)
                                .activo(true).build());
                adicionales.add(Adicional.builder().nombre("Frijoles Refritos Bayos").precio(5500.0).activo(true)
                                .build());
                adicionales.add(Adicional.builder().nombre("Chicharrón de Cerdo Crujiente").precio(6900.0).activo(true)
                                .build());

                // Burritos extras
                adicionales.add(Adicional.builder().nombre("Arroz Rojo a la Mexicana").precio(4900.0).activo(true)
                                .build());
                adicionales.add(Adicional.builder().nombre("Jalapeños en Escabeche Caseros").precio(3900.0).activo(true)
                                .build());
                adicionales.add(Adicional.builder().nombre("Salsa Verde Taquera Extra").precio(3500.0).activo(true)
                                .build());

                for (int i = 0; i < adicionales.size(); i++) {
                        adicionales.set(i, adicionalRepository.save(adicionales.get(i)));
                }

                // Asociar a categorías en la tabla intermedia categoria_adicional (relación
                // Many-to-Many con adicionales compartidos)
                catTacos.getAdicionales().addAll(List.of(
                                adicionales.get(0), // Cebollitas Cambray Asadas
                                adicionales.get(1), // Salsa Habanera Tatemada Extra (compartido)
                                adicionales.get(2), // Porción Piña Asada Caramelizada
                                adicionales.get(3), // Queso Oaxaca Extra Fundido (compartido)
                                adicionales.get(4), // Guacamole Tradicional Extra (compartido)
                                adicionales.get(7) // Pico de Gallo Fresco Casero (compartido)
                ));

                catQuesadillas.getAdicionales().addAll(List.of(
                                adicionales.get(3), // Queso Oaxaca Extra Fundido (compartido)
                                adicionales.get(4), // Guacamole Tradicional Extra (compartido)
                                adicionales.get(5), // Chorizo Casero a la Plancha
                                adicionales.get(1), // Salsa Habanera Tatemada Extra (compartido)
                                adicionales.get(13) // Jalapeños en Escabeche Caseros (compartido)
                ));

                catEntradas.getAdicionales().addAll(List.of(
                                adicionales.get(6), // Totopos Artesanales Crujientes
                                adicionales.get(7), // Pico de Gallo Fresco Casero (compartido)
                                adicionales.get(8), // Crema Ácida Mexicana Especial
                                adicionales.get(4), // Guacamole Tradicional Extra (compartido)
                                adicionales.get(3) // Queso Oaxaca Extra Fundido (compartido)
                ));

                catEspecialidades.getAdicionales().addAll(List.of(
                                adicionales.get(9), // Consomé de Birria para Sumergir
                                adicionales.get(10), // Frijoles Refritos Bayos (compartido)
                                adicionales.get(11), // Chicharrón de Cerdo Crujiente
                                adicionales.get(1), // Salsa Habanera Tatemada Extra (compartido)
                                adicionales.get(3) // Queso Oaxaca Extra Fundido (compartido)
                ));

                catBurritos.getAdicionales().addAll(List.of(
                                adicionales.get(12), // Arroz Rojo a la Mexicana
                                adicionales.get(13), // Jalapeños en Escabeche Caseros (compartido)
                                adicionales.get(14), // Salsa Verde Taquera Extra
                                adicionales.get(3), // Queso Oaxaca Extra Fundido (compartido)
                                adicionales.get(4), // Guacamole Tradicional Extra (compartido)
                                adicionales.get(10) // Frijoles Refritos Bayos (compartido)
                ));

                categoriaRepository.save(catTacos);
                categoriaRepository.save(catQuesadillas);
                categoriaRepository.save(catEntradas);
                categoriaRepository.save(catEspecialidades);
                categoriaRepository.save(catBurritos);
                categoriaRepository.save(catBebidas);

                // 6. Crear perfiles del personal del restaurante

                // Domiciliarios
                List<Domiciliario> domiciliarios = new ArrayList<>();
                domiciliarios.add(Domiciliario.builder().cedula("1010203040").nombre("Carlos Gómez")
                                .celular("+57 311 222 3333").contrasena("domi123").disponible(true).build());
                domiciliarios.add(Domiciliario.builder().cedula("1020304050").nombre("Andrés Parra")
                                .celular("+57 312 444 5555").contrasena("domi123").disponible(true).build());
                domiciliarios.add(Domiciliario.builder().cedula("1030405060").nombre("Mateo Quintana")
                                .celular("+57 315 666 7777").contrasena("domi123").disponible(true).build());
                domiciliarios.add(Domiciliario.builder().cedula("1040506070").nombre("Javier Morales")
                                .celular("+57 318 888 9999").contrasena("domi123").disponible(true).build());
                domiciliarios.add(Domiciliario.builder().cedula("1050607080").nombre("Sebastián Roa")
                                .celular("+57 320 111 2222").contrasena("domi123").disponible(false).build());

                for (Domiciliario d : domiciliarios) {
                        domiciliarioRepository.save(d);
                }

                // Administrador
                administradorRepository.save(
                                Administrador.builder().usuario("admin_elwey").contrasena("admin123").build());

                // Operadores
                operadorRepository.save(Operador.builder().nombre("Juan Camilo Chef").usuario("chef_camilo")
                                .contrasena("cocina123").build());
                operadorRepository.save(Operador.builder().nombre("Paola Despachos").usuario("paola_despachos")
                                .contrasena("despacho123").build());

                // 7. Crear pedidos iniciales con sus Items, Comidas y Adicionales (con cantidad
                // en tabla intermedia)
                List<Cliente> clientesEnDb = clienteRepository.findAll();
                Cliente messi = clientesEnDb.get(0);
                Cliente cr7 = clientesEnDb.get(1);
                Cliente falcao = clientesEnDb.get(2);
                Cliente james = clientesEnDb.get(3);
                Cliente lucho = clientesEnDb.get(4);
                Cliente neymar = clientesEnDb.get(5);

                // Pedido 1: Messi
                Pedido ped1 = Pedido.builder()
                                .cliente(messi)
                                .domiciliario(domiciliarios.get(0))
                                .estado("Enviado")
                                .fechaCreacion(LocalDateTime.now().minusMinutes(45))
                                .build();
                ped1 = pedidoRepository.save(ped1);

                ItemPedido item1Ped1 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped1)
                                .comida(comidas.get(0)) // Tacos al Pastor (18000)
                                .cantidad(2)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item1Ped1)
                                .adicional(adicionales.get(0)).cantidad(2).build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item1Ped1)
                                .adicional(adicionales.get(2)).cantidad(1).build());

                ItemPedido item2Ped1 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped1)
                                .comida(comidas.get(2)) // Guacamole El Wey (15000)
                                .cantidad(1)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item2Ped1)
                                .adicional(adicionales.get(6)).cantidad(1).build());

                // Pedido 2: CR7
                Pedido ped2 = Pedido.builder()
                                .cliente(cr7)
                                .domiciliario(null)
                                .estado("Cocinando")
                                .fechaCreacion(LocalDateTime.now().minusMinutes(20))
                                .build();
                ped2 = pedidoRepository.save(ped2);

                ItemPedido item1Ped2 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped2)
                                .comida(comidas.get(1)) // Quesadilla de Birria (22000)
                                .cantidad(1)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item1Ped2)
                                .adicional(adicionales.get(9)).cantidad(1).build());

                ItemPedido item2Ped2 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped2)
                                .comida(comidas.get(4)) // Burrito Norteño (24000)
                                .cantidad(1)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item2Ped2)
                                .adicional(adicionales.get(12)).cantidad(2).build());

                // Pedido 3: Falcao
                Pedido ped3 = Pedido.builder()
                                .cliente(falcao)
                                .domiciliario(domiciliarios.get(2))
                                .estado("Entregado")
                                .fechaCreacion(LocalDateTime.now().minusHours(3))
                                .fechaEntrega(LocalDateTime.now().minusHours(2))
                                .build();
                ped3 = pedidoRepository.save(ped3);

                ItemPedido item1Ped3 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped3)
                                .comida(comidas.get(6)) // Nachos El Wey (23000)
                                .cantidad(1)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item1Ped3)
                                .adicional(adicionales.get(4)).cantidad(1).build());

                ItemPedido item2Ped3 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped3)
                                .comida(comidas.get(7)) // Gringa con Carne (21000)
                                .cantidad(2)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item2Ped3)
                                .adicional(adicionales.get(3)).cantidad(2).build());

                // Pedido 4: James Rodríguez (Listo en barra, pre-asignado a Carlos Gómez)
                Pedido ped4 = Pedido.builder()
                                .cliente(james)
                                .domiciliario(domiciliarios.get(0))
                                .estado("Listo")
                                .fechaCreacion(LocalDateTime.now().minusMinutes(15))
                                .build();
                ped4 = pedidoRepository.save(ped4);

                ItemPedido item1Ped4 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped4)
                                .comida(comidas.get(3)) // Enchiladas Rojas (20000)
                                .cantidad(1)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item1Ped4)
                                .adicional(adicionales.get(10)).cantidad(1).build());

                ItemPedido item2Ped4 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped4)
                                .comida(comidas.get(5)) // Tacos de Suadero (19000)
                                .cantidad(2)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item2Ped4)
                                .adicional(adicionales.get(1)).cantidad(1).build());

                // Pedido 5: Luis Díaz
                Pedido ped5 = Pedido.builder()
                                .cliente(lucho)
                                .domiciliario(domiciliarios.get(4))
                                .estado("Entregado")
                                .fechaCreacion(LocalDateTime.now().minusHours(5))
                                .fechaEntrega(LocalDateTime.now().minusHours(4))
                                .build();
                ped5 = pedidoRepository.save(ped5);

                ItemPedido item1Ped5 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped5)
                                .comida(comidas.get(28)) // Burrito California (25000)
                                .cantidad(1)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item1Ped5)
                                .adicional(adicionales.get(13)).cantidad(1).build());

                ItemPedido item2Ped5 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped5)
                                .comida(comidas.get(11)) // Quesadilla de Chicharrón Prensado (19000)
                                .cantidad(1)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item2Ped5)
                                .adicional(adicionales.get(3)).cantidad(1).build());

                // Pedido 6: Neymar Jr
                Pedido ped6 = Pedido.builder()
                                .cliente(neymar)
                                .domiciliario(domiciliarios.get(1))
                                .estado("Enviado")
                                .fechaCreacion(LocalDateTime.now().minusMinutes(35))
                                .build();
                ped6 = pedidoRepository.save(ped6);

                ItemPedido item1Ped6 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped6)
                                .comida(comidas.get(10)) // Tacos Gobernador (25000)
                                .cantidad(2)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item1Ped6)
                                .adicional(adicionales.get(0)).cantidad(1).build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item1Ped6)
                                .adicional(adicionales.get(1)).cantidad(2).build());

                // Pedido 7: Entregado previamente por Carlos Gómez (para historial de entregas)
                Pedido ped7 = Pedido.builder()
                                .cliente(falcao)
                                .domiciliario(domiciliarios.get(0))
                                .estado("Entregado")
                                .fechaCreacion(LocalDateTime.now().minusHours(2))
                                .fechaEntrega(LocalDateTime.now().minusHours(1))
                                .build();
                ped7 = pedidoRepository.save(ped7);

                ItemPedido item1Ped7 = itemPedidoRepository.save(ItemPedido.builder()
                                .pedido(ped7)
                                .comida(comidas.get(0))
                                .cantidad(3)
                                .build());
                itemPedidoAdicionalRepository.save(ItemPedidoAdicional.builder().itemPedido(item1Ped7)
                                .adicional(adicionales.get(4)).cantidad(1).build());
        }
}
