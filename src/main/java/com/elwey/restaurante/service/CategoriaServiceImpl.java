package com.elwey.restaurante.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elwey.restaurante.dto.CategoriaAdicionalesRequest;
import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.entities.Comida;
import com.elwey.restaurante.errors.AdicionalNotFoundException;
import com.elwey.restaurante.errors.CategoriaNotFoundException;
import com.elwey.restaurante.repository.AdicionalRepository;
import com.elwey.restaurante.repository.CategoriaRepository;
import com.elwey.restaurante.repository.ComidaRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository repo;

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    Logger log = LoggerFactory.getLogger(CategoriaServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> seeAll() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria searchById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodos() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public String obtenerNombreCategoriaPorComidaId(Long comidaId) {
        return comidaRepository.findById(comidaId)
                .map(c -> c.getCategoria() != null ? c.getCategoria().getNombre() : "Sin Categoría")
                .orElse("Sin Categoría");
    }

    @Override
    @Transactional
    public void asignarComida(Long categoriaId, Comida comida) {
        Categoria cat = repo.findById(categoriaId)
                .orElseThrow(() -> new CategoriaNotFoundException(categoriaId));
        comida.setCategoria(cat);
        comidaRepository.save(comida);
    }

    @Override
    @Transactional
    public void quitarComida(Long comidaId) {
        comidaRepository.findById(comidaId).ifPresent(c -> {
            c.setCategoria(null);
            comidaRepository.save(c);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Long obtenerIdPorComidaId(Long comidaId) {
        return comidaRepository.findById(comidaId)
                .map(c -> c.getCategoria() != null ? c.getCategoria().getId() : null)
                .orElse(null);
    }

    @Override
    @Transactional
    public void actualizarAdicionales(CategoriaAdicionalesRequest request) {
        if (request == null || request.getCategoriaId() == null || request.getAdicionalesIds() == null) {
            throw new IllegalArgumentException("Debe indicar categoriaId y la lista de adicionales.");
        }

        Long categoriaId = request.getCategoriaId();
        List<Long> adicionalIds = request.getAdicionalesIds();
        if (adicionalIds.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("La lista de adicionales no puede contener IDs nulos.");
        }

        log.info("Petición REST: actualizar adicionales de la categoría ID: {} ({} seleccionados)",
                categoriaId, adicionalIds.size());

        Categoria categoria = repo.findById(categoriaId)
                .orElseThrow(() -> new CategoriaNotFoundException(categoriaId));

        Set<Long> idsSeleccionados = new LinkedHashSet<>(adicionalIds);
        List<Adicional> adicionalesSeleccionados = new ArrayList<>();
        for (Long adicionalId : idsSeleccionados) {
            adicionalesSeleccionados.add(adicionalRepository.findById(adicionalId)
                    .orElseThrow(() -> new AdicionalNotFoundException(adicionalId)));
        }

        if (categoria.getAdicionales() == null) {
            categoria.setAdicionales(new ArrayList<>());
        }

        List<Adicional> adicionalesAnteriores = new ArrayList<>(categoria.getAdicionales());
        for (Adicional anterior : adicionalesAnteriores) {
            if (anterior.getCategorias() != null) {
                anterior.getCategorias()
                        .removeIf(categoriaAsociada -> categoriaId.equals(categoriaAsociada.getId()));
            }
        }

        categoria.getAdicionales().clear();
        categoria.getAdicionales().addAll(adicionalesSeleccionados);
        for (Adicional adicional : adicionalesSeleccionados) {
            if (adicional.getCategorias() == null) {
                adicional.setCategorias(new ArrayList<>());
            }
            if (adicional.getCategorias().stream()
                    .noneMatch(categoriaAsociada -> categoriaId.equals(categoriaAsociada.getId()))) {
                adicional.getCategorias().add(categoria);
            }
        }

        repo.save(categoria);
    }

}
