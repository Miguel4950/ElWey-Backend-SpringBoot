package com.elwey.restaurante.service;

import java.util.List;

import com.elwey.restaurante.dto.CategoriaAdicionalesRequest;
import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.entities.Categoria;
import com.elwey.restaurante.entities.Comida;

public interface CategoriaService {

    List<Categoria> seeAll();

    Categoria searchById(Long id);

    List<Categoria> listarTodos();

    String obtenerNombreCategoriaPorComidaId(Long comidaId);

    void asignarComida(Long categoriaId, Comida comida);

    void quitarComida(Long comidaId);

    Long obtenerIdPorComidaId(Long comidaId);

    void actualizarAdicionales(CategoriaAdicionalesRequest request);
}
