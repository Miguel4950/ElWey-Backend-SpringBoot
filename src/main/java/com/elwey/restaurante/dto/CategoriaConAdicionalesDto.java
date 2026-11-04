package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.Categoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaConAdicionalesDto {

    private Long id;
    private String nombre;
    private List<Long> adicionalesIds;

    public static CategoriaConAdicionalesDto fromEntity(Categoria categoria) {
        List<Long> adicionalesIds = categoria.getAdicionales() == null
                ? Collections.emptyList()
                : categoria.getAdicionales().stream()
                        .map(adicional -> adicional.getId())
                        .collect(Collectors.toList());

        return CategoriaConAdicionalesDto.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .adicionalesIds(adicionalesIds)
                .build();
    }
}
