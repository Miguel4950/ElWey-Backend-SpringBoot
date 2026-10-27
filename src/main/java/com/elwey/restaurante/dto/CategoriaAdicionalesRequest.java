package com.elwey.restaurante.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoriaAdicionalesRequest {

    private Long categoriaId;
    private List<AdicionalSeleccionRequest> adicionales;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AdicionalSeleccionRequest {
        private Long id;
        private Boolean seleccionado;
    }
}
