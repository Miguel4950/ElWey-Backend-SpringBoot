package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.ComidaDto;
import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.service.CategoriaService;
import com.elwey.restaurante.service.ComidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para el catálogo de comidas del restaurante "El Wey".
 * Permite operaciones CRUD completas y gestión de la lista de adicionales disponibles por comida.
 */
@Tag(name = "Comidas", description = "Endpoints para el catálogo de platos y sus adicionales disponibles")
@RestController
@RequestMapping({ "/api/comidas", "/comidas" })
@CrossOrigin(origins = "http://localhost:4200")
public class ComidaController {

    @Autowired
    private ComidaService service;

    @Autowired
    private CategoriaService categoriaService;

    private static final Logger log = LoggerFactory.getLogger(ComidaController.class);

    @Operation(summary = "Listar todas las comidas registradas")
    @GetMapping({ "", "/", "/all", "/tabla", "/tarjetas" })
    public ResponseEntity<List<ComidaDto>> seeAll() {
        log.info("Petición REST: consultar todas las comidas");
        List<ComidaDto> dtos = service.seeAll().stream()
                .map(ComidaDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Listar comidas activas para el menú digital")
    @GetMapping({ "/activos", "/activas" })
    public ResponseEntity<List<ComidaDto>> verComidasActivas() {
        log.info("Petición REST: consultar comidas activas desde base de datos");
        List<ComidaDto> dtos = service.listarActivas().stream()
                .map(ComidaDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Consultar el detalle de una comida con sus adicionales disponibles")
    @GetMapping({ "/{id}", "/find/{id}" })
    public ResponseEntity<ComidaDto> findById(@PathVariable("id") Long id) {
        log.info("Petición REST: consultar comida con ID: {}", id);
        Comida comida = service.searchById(id);
        List<Adicional> adicionales = service.obtenerAdicionalesDisponibles(id);
        return ResponseEntity.ok(ComidaDto.fromEntityWithAdicionales(comida, adicionales));
    }

    @Operation(summary = "Buscar comidas por nombre o categoría")
    @GetMapping("/buscar")
    public ResponseEntity<List<ComidaDto>> buscar(@RequestParam(name = "q", required = false) String query) {
        log.info("Petición REST: buscar comidas con término: {}", query);
        List<ComidaDto> dtos = service.buscarPorNombre(query).stream()
                .map(ComidaDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Crear un nuevo plato de comida con su categoría y adicionales disponibles")
    @PostMapping({ "", "/", "/add", "/guardar" })
    public ResponseEntity<Void> addComida(@RequestBody ComidaDto dto) {
        log.info("Petición REST: crear plato: {} con categoría ID: {} y adicionales: {}",
                dto.getNombre(), dto.getCategoriaId(), dto.getAdicionalesIds());
        Comida comida = dto.toEntity();
        service.save(comida, dto.getCategoriaId(), dto.getAdicionalesIds());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Actualizar un plato existente y modificar su lista de adicionales disponibles")
    @PutMapping({ "/{id}", "/update" })
    public ResponseEntity<Void> updateComida(
            @PathVariable(name = "id", required = false) Long id,
            @RequestBody ComidaDto dto) {
        Long targetId = (id != null) ? id : dto.getId();
        log.info("Petición REST: actualizar plato ID: {} con adicionales: {}", targetId, dto.getAdicionalesIds());
        Comida comida = dto.toEntity();
        if (targetId != null) {
            comida.setId(targetId);
        }
        service.save(comida, dto.getCategoriaId(), dto.getAdicionalesIds());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Eliminar un plato por ID si no tiene pedidos históricos")
    @DeleteMapping({ "/{id}", "/delete/{id}" })
    public ResponseEntity<Void> deleteComida(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminar comida con ID: {}", id);
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar categorías disponibles para selección en formulario de comida")
    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> obtenerCategorias() {
        log.info("Petición REST: consultar categorías para comidas");
        return ResponseEntity.ok(categoriaService.seeAll());
    }
}
