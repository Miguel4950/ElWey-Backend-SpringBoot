package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.errors.CategoriaNotFoundException;
import com.elwey.restaurante.errors.ComidaEnUsoException;
import com.elwey.restaurante.errors.ComidaNotFoundException;
import com.elwey.restaurante.repository.CategoriaRepository;
import com.elwey.restaurante.repository.ComidaRepository;
import com.elwey.restaurante.repository.ItemPedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la capa de servicio para Comida.
 * Maneja la lógica de negocio y la asociación de la comida con su categoría.
 */
@Service
public class ComidaServiceImpl implements ComidaService {

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Comida> listarTodos() {
        return comidaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comida> listarActivas() {
        return comidaRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Comida obtenerPorId(Long id) {
        return comidaRepository.findById(id)
                .orElseThrow(() -> new ComidaNotFoundException(id));
    }

    @Override
    @Transactional
    public void save(Comida comida, Long categoriaId) {
        if (categoriaId != null) {
            Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new CategoriaNotFoundException(categoriaId));
            comida.setCategoria(categoria);
        }
        comidaRepository.save(comida);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Comida comida = comidaRepository.findById(id)
                .orElseThrow(() -> new ComidaNotFoundException(id));

        if (!itemPedidoRepository.findByComidaId(id).isEmpty()) {
            throw new ComidaEnUsoException(id, comida.getNombre());
        }

        comidaRepository.delete(comida);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comida> buscarPorNombre(String query) {
        if (query == null || query.trim().isEmpty()) {
            return listarTodos();
        }
        String cleanQuery = query.trim();
        return comidaRepository.findByNombreContainingIgnoreCaseOrCategoriaNombreContainingIgnoreCase(cleanQuery, cleanQuery);
    }
}
