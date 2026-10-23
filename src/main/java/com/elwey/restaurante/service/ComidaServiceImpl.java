package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.errors.CategoriaNotFoundException;
import com.elwey.restaurante.errors.ComidaEnUsoException;
import com.elwey.restaurante.errors.ComidaNotFoundException;
import com.elwey.restaurante.repository.AdicionalRepository;
import com.elwey.restaurante.repository.CategoriaRepository;
import com.elwey.restaurante.repository.ComidaRepository;
import com.elwey.restaurante.repository.ItemPedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la capa de servicio para Comida.
 * Maneja lógica transaccional de negocio, asociación de categoría y relación ManyToMany con adicionales disponibles.
 */
@Service
public class ComidaServiceImpl implements ComidaService {

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Comida> seeAll() {
        return comidaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comida> listarActivas() {
        return comidaRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Comida searchById(Long id) {
        return comidaRepository.findById(id)
                .orElseThrow(() -> new ComidaNotFoundException(id));
    }

    @Override
    @Transactional
    public void save(Comida comida, Long categoriaId) {
        save(comida, categoriaId, null);
    }

    @Override
    @Transactional
    public void save(Comida comida, Long categoriaId, List<Long> adicionalesIds) {
        if (categoriaId != null) {
            Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new CategoriaNotFoundException(categoriaId));
            comida.setCategoria(categoria);
        }

        // Asociar adicionales disponibles para este plato específico
        if (adicionalesIds != null && !adicionalesIds.isEmpty()) {
            List<Adicional> seleccionados = adicionalRepository.findAllById(adicionalesIds);
            comida.setAdicionalesDisponibles(new ArrayList<>(seleccionados));
        } else if (comida.getId() == null && comida.getCategoria() != null && comida.getCategoria().getAdicionales() != null) {
            // Si es un plato nuevo y no se especificaron IDs, se heredan los de su categoría por defecto
            comida.setAdicionalesDisponibles(new ArrayList<>(comida.getCategoria().getAdicionales()));
        } else if (adicionalesIds != null && adicionalesIds.isEmpty() && comida.getAdicionalesDisponibles() != null) {
            // Si explícitamente se suministra una lista vacía para edición, se limpia
            comida.getAdicionalesDisponibles().clear();
        }

        comidaRepository.save(comida);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Adicional> obtenerAdicionalesDisponibles(Long comidaId) {
        Comida comida = searchById(comidaId);
        if (comida.getAdicionalesDisponibles() != null && !comida.getAdicionalesDisponibles().isEmpty()) {
            return comida.getAdicionalesDisponibles();
        }
        // Respaldo de compatibilidad: si la lista directa no está poblada, se consultan los de la categoría
        if (comida.getCategoria() != null && comida.getCategoria().getAdicionales() != null) {
            return comida.getCategoria().getAdicionales();
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Comida comida = comidaRepository.findById(id)
                .orElseThrow(() -> new ComidaNotFoundException(id));

        if (!itemPedidoRepository.findByComidaId(id).isEmpty()) {
            throw new ComidaEnUsoException(id, comida.getNombre());
        }

        if (comida.getAdicionalesDisponibles() != null) {
            comida.getAdicionalesDisponibles().clear();
        }

        comidaRepository.delete(comida);
    }

    // Métodos de compatibilidad con vistas existentes
    @Override
    @Transactional(readOnly = true)
    public List<Comida> listarTodas() {
        return seeAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comida> listarTodasConCategoria() {
        return seeAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Comida obtenerPorId(Long id) {
        return searchById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Comida obtenerEntidadPorId(Long id) {
        return searchById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comida> buscarPorNombre(String query) {
        if (query == null || query.trim().isEmpty()) {
            return seeAll();
        }
        String cleanQuery = query.trim();
        return comidaRepository.findByNombreContainingIgnoreCaseOrCategoriaNombreContainingIgnoreCase(cleanQuery, cleanQuery);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        deleteById(id);
    }
}
