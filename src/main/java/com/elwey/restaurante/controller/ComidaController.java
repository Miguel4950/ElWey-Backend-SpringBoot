package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.ComidaDto;
import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.service.CategoriaService;
import com.elwey.restaurante.service.ComidaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({ "/api/comidas", "/comidas" })
@CrossOrigin(origins = "http://localhost:4200")
public class ComidaController {

    @Autowired
    private ComidaService service;

    @Autowired
    private CategoriaService categoriaService;

    private static final Logger log = LoggerFactory.getLogger(ComidaController.class);

    /**
     * GET /api/comidas
     * Retorna lista de platos con categoriaId y categoriaNombre aplanados. (Status: 200 OK).
     */
    @GetMapping({ "", "/", "/all", "/tabla", "/tarjetas" })
    public ResponseEntity<List<ComidaDto>> seeAll() {
        log.info("Petición REST: consultar todas las comidas");
        List<ComidaDto> dtos = service.seeAll().stream()
                .map(ComidaDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/comidas/activos, GET /comidas/activos
     * Retorna lista de platos activos filtrados directamente en la base de datos
     * con categoriaId y categoriaNombre aplanados. (Status: 200 OK).
     */
    @GetMapping({ "/activos", "/activas" })
    public ResponseEntity<List<ComidaDto>> verComidasActivas() {
        log.info("Petición REST: consultar comidas activas desde base de datos");
        List<ComidaDto> dtos = service.listarActivas().stream()
                .map(ComidaDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/comidas/{id}
     * Retorna el plato por ID incluyendo el array adicionales: [...]. (Status: 200 OK o 404 Not Found).
     */
    @GetMapping({ "/{id}", "/find/{id}" })
    public ResponseEntity<ComidaDto> findById(@PathVariable("id") Long id) {
        log.info("Petición REST: consultar comida con ID: {}", id);
        Comida comida = service.searchById(id);
        List<Adicional> adicionales = (comida.getCategoria() != null && comida.getCategoria().getAdicionales() != null)
                ? comida.getCategoria().getAdicionales()
                : new ArrayList<>();
        return ResponseEntity.ok(ComidaDto.fromEntityWithAdicionales(comida, adicionales));
    }

    /**
     * GET /api/comidas/buscar?q=...
     * Busca comidas por término en el nombre o categoría.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ComidaDto>> buscar(@RequestParam(name = "q", required = false) String query) {
        log.info("Petición REST: buscar comidas con término: {}", query);
        List<ComidaDto> dtos = service.buscarPorNombre(query).stream()
                .map(ComidaDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * POST /api/comidas
     * Recibe { nombre, precio, descripcion, imagen_url, etiqueta, activo, categoriaId }
     * Retorna el plato creado con su ID. (Status: 201 Created).
     */
    @PostMapping({ "", "/", "/add", "/guardar" })
    public ResponseEntity<Void> addComida(@RequestBody ComidaDto dto) {
        log.info("Petición REST: crear plato: {} con categoría ID: {}", dto.getNombre(), dto.getCategoriaId());
        Comida comida = dto.toEntity();
        service.save(comida, dto.getCategoriaId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * PUT /api/comidas/{id}
     * Recibe los datos actualizados del plato y retorna el objeto actualizado. (Status: 200 OK).
     */
    @PutMapping({ "/{id}", "/update" })
    public ResponseEntity<Void> updateComida(
            @PathVariable(name = "id", required = false) Long id,
            @RequestBody ComidaDto dto) {
        Long targetId = (id != null) ? id : dto.getId();
        log.info("Petición REST: actualizar plato ID: {}", targetId);
        Comida comida = dto.toEntity();
        if (targetId != null) {
            comida.setId(targetId);
        }
        service.save(comida, dto.getCategoriaId());
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/comidas/{id}
     * Elimina el plato o lo desactiva. (Status: 204 No Content).
     */
    @DeleteMapping({ "/{id}", "/delete/{id}" })
    public ResponseEntity<Void> deleteComida(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminar comida con ID: {}", id);
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/comidas/categorias
     * Auxiliar para consultar las categorías disponibles para el formulario de comida.
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> obtenerCategorias() {
        log.info("Petición REST: consultar categorías para comidas");
        return ResponseEntity.ok(categoriaService.seeAll());
    }
}