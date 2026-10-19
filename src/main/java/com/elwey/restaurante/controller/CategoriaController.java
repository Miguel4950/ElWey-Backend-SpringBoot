package com.elwey.restaurante.controller;

import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.service.CategoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({ "/api/categorias", "/categorias" })
@CrossOrigin(origins = "http://localhost:4200")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    private static final Logger log = LoggerFactory.getLogger(CategoriaController.class);

    /**
     * GET /api/categorias
     * Retorna [ { "id": 1, "nombre": "Tacos" }, ... ]. (Status: 200 OK).
     */
    @GetMapping({ "", "/" })
    public ResponseEntity<List<Categoria>> listarCategorias() {
        log.info("Petición REST: listar categorías");
        return ResponseEntity.ok(categoriaService.seeAll());
    }

    /**
     * GET /api/categorias/{id}
     * Retorna una categoría por su ID. (Status: 200 OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoria(@PathVariable("id") Long id) {
        log.info("Petición REST: consultar categoría con ID: {}", id);
        return ResponseEntity.ok(categoriaService.searchById(id));
    }
}
