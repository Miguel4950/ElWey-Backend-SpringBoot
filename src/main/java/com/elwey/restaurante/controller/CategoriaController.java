package com.elwey.restaurante.controller;

import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la consulta de categorías gastronómicas del restaurante.
 */
@Tag(name = "Categorías", description = "Endpoints para la consulta de categorías de platos")
@RestController
@RequestMapping({ "/categorias", "/categoria" })
@CrossOrigin(origins = "http://localhost:4200")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    private static final Logger log = LoggerFactory.getLogger(CategoriaController.class);

    @Operation(summary = "Listar todas las categorías del menú")
    @GetMapping({ "", "/", "/all" })
    public ResponseEntity<List<Categoria>> listarCategorias() {
        log.info("Petición REST: listar categorías");
        return ResponseEntity.ok(categoriaService.seeAll());
    }

    @Operation(summary = "Obtener una categoría por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoria(@PathVariable("id") Long id) {
        log.info("Petición REST: consultar categoría con ID: {}", id);
        return ResponseEntity.ok(categoriaService.searchById(id));
    }
}
