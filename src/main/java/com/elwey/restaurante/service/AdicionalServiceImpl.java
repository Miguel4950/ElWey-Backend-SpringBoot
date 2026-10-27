package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.errors.AdicionalNotFoundException;
import com.elwey.restaurante.repository.AdicionalRepository;
import com.elwey.restaurante.repository.ComidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la capa de servicio para Adicional.
 * Proporciona lógica transaccional para CRUD y desvinculación segura en relaciones ManyToMany con Categoria.
 */
@Service
public class AdicionalServiceImpl implements AdicionalService {

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ComidaRepository comidaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Adicional> findAll() {
        return adicionalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Adicional> listarActivos() {
        return adicionalRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Adicional findById(Long id) {
        return adicionalRepository.findById(id)
                .orElseThrow(() -> new AdicionalNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Adicional> findByComidaId(Long comidaId) {
        Comida comida = comidaRepository.findById(comidaId).orElse(null);
        if (comida != null && comida.getCategoria() != null) {
            return adicionalRepository.findByCategoriasId(comida.getCategoria().getId());
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Adicional> findByCategoriaId(Long categoriaId) {
        return adicionalRepository.findByCategoriasId(categoriaId);
    }

    @Override
    @Transactional
    public Adicional save(Adicional adicional) {
        return adicionalRepository.save(adicional);
    }

    @Override
    @Transactional
    public void save(Adicional adicional, Long categoriaId) {
        if (categoriaId != null) {
            Categoria categoria = categoriaService.searchById(categoriaId);
            if (adicional.getCategorias() == null) {
                adicional.setCategorias(new ArrayList<>());
            }
            if (!adicional.getCategorias().contains(categoria)) {
                adicional.getCategorias().add(categoria);
            }
            if (categoria.getAdicionales() == null) {
                categoria.setAdicionales(new ArrayList<>());
            }
            if (!categoria.getAdicionales().contains(adicional)) {
                categoria.getAdicionales().add(adicional);
            }
        }
        adicionalRepository.save(adicional);
    }

    @Override
    @Transactional
    public Adicional update(Long id, Adicional adicionalActualizado) {
        Adicional existente = findById(id);
        if (adicionalActualizado.getNombre() != null && !adicionalActualizado.getNombre().isBlank()) {
            existente.setNombre(adicionalActualizado.getNombre().trim());
        }
        if (adicionalActualizado.getPrecio() != null) {
            existente.setPrecio(adicionalActualizado.getPrecio());
        }
        if (adicionalActualizado.getActivo() != null) {
            existente.setActivo(adicionalActualizado.getActivo());
        }
        return adicionalRepository.save(existente);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Adicional adicional = findById(id);

        // Desvincular de las categorías dueñas de la relación ManyToMany (tabla categoria_adicional)
        if (adicional.getCategorias() != null) {
            for (Categoria c : adicional.getCategorias()) {
                if (c.getAdicionales() != null) {
                    c.getAdicionales().remove(adicional);
                }
            }
            adicional.getCategorias().clear();
        }

        // Eliminar físicamente el adicional sin violar restricciones de llave foránea
        adicionalRepository.delete(adicional);
    }
}
