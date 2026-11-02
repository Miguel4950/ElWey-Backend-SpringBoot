package com.elwey.restaurante.dto;

import java.util.List;

import com.elwey.restaurante.entities.Adicional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdicionalListadoDto {

    private Long id;
    private String nombre;
    private Double precio;
    private List<Long> categoriasId;

    public static AdicionalListadoDto fromEntity(Adicional adicional) {
        if (adicional == null) {
            return null;
        }

        List<Long> categoriasId = adicional.getCategorias() == null
                ? List.of()
                : adicional.getCategorias().stream()
                        .map(categoria -> categoria.getId())
                        .toList();

        return AdicionalListadoDto.builder()
                .id(adicional.getId())
                .nombre(adicional.getNombre())
                .precio(adicional.getPrecio())
                .categoriasId(categoriasId)
                .build();
    }
}