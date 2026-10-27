package com.elwey.restaurante.controller;

import com.elwey.restaurante.dto.AdicionalDto;
import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.service.AdicionalService;
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
 * Controlador REST para la gestión de Adicionales (extras e ingredientes).
 * Expone endpoints para el CRUD completo y filtrado por comida utilizando AdicionalDto.
 */
@Tag(name = "Adicionales", description = "Endpoints para la gestión y consulta de adicionales e ingredientes extra")
@RestController
@RequestMapping({ "/adicionales", "/adicional" })
@CrossOrigin(origins = "http://localhost:4200")
public class AdicionalController {

    @Autowired
    private AdicionalService adicionalService;

    private static final Logger log = LoggerFactory.getLogger(AdicionalController.class);

    @Operation(summary = "Listar todos los adicionales registrados")
    @GetMapping({ "", "/", "/all" })
    public ResponseEntity<List<AdicionalDto>> listarTodos() {
        log.info("Petición REST: listar todos los adicionales");
        List<AdicionalDto> dtos = adicionalService.findAll().stream()
                .map(AdicionalDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener un adicional por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<AdicionalDto> obtenerPorId(@PathVariable("id") Long id) {
        log.info("Petición REST: obtener adicional con ID: {}", id);
        return ResponseEntity.ok(AdicionalDto.fromEntity(adicionalService.findById(id)));
    }

    @Operation(summary = "Listar los adicionales disponibles para una comida específica")
    @GetMapping("/comida/{comidaId}")
    public ResponseEntity<List<AdicionalDto>> listarPorComida(@PathVariable("comidaId") Long comidaId) {
        log.info("Petición REST: listar adicionales para la comida ID: {}", comidaId);
        List<AdicionalDto> dtos = adicionalService.findByComidaId(comidaId).stream()
                .map(AdicionalDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Crear un nuevo adicional en el sistema")
    @PostMapping({ "", "/", "/add" })
    public ResponseEntity<AdicionalDto> crearAdicional(
            @RequestBody AdicionalDto dto,
            @RequestParam(name = "categoriaId", required = false) Long categoriaId) {
        Long catId = (categoriaId != null) ? categoriaId : dto.getCategoriaId();
        log.info("Petición REST: crear adicional: {} con categoría opcional ID: {}", dto.getNombre(), catId);
        Adicional adicional = dto.toEntity();
        Adicional nuevo = adicionalService.save(adicional, catId);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdicionalDto.fromEntity(nuevo));
    }

    @Operation(summary = "Actualizar información de un adicional existente")
    @PutMapping({ "/{id}", "/update/{id}" })
    public ResponseEntity<AdicionalDto> actualizarAdicional(
            @PathVariable("id") Long id,
            @RequestBody AdicionalDto dto) {
        log.info("Petición REST: actualizar adicional con ID: {}", id);
        Adicional actualizado = adicionalService.update(id, dto.toEntity());
        return ResponseEntity.ok(AdicionalDto.fromEntity(actualizado));
    }

    @Operation(summary = "Eliminar de forma segura un adicional desvinculándolo de sus relaciones")
    @DeleteMapping({ "/{id}", "/delete/{id}" })
    public ResponseEntity<Void> eliminarAdicional(@PathVariable("id") Long id) {
        log.info("Petición REST: eliminar adicional con ID: {}", id);
        adicionalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
