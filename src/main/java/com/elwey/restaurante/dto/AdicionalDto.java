package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.Adicional;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * DTO plano para la transferencia de datos de Adicional hacia y desde el Frontend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdicionalDto {

    private Long id;
    private String nombre;
    private Double precio;
    private Boolean activo;
    private Long categoriaId;
    private String categoriaNombre;

    public static AdicionalDto fromEntity(Adicional adicional) {
        if (adicional == null) return null;
        Long catId = (adicional.getCategoria() != null) ? adicional.getCategoria().getId() : null;
        String catNombre = (adicional.getCategoria() != null) ? adicional.getCategoria().getNombre() : null;

        return AdicionalDto.builder()
                .id(adicional.getId())
                .nombre(adicional.getNombre())
                .precio(adicional.getPrecio())
                .activo(adicional.getActivo())
                .categoriaId(catId)
                .categoriaNombre(catNombre)
                .build();
    }

    public Adicional toEntity() {
        return Adicional.builder()
                .id(this.id)
                .nombre(this.nombre)
                .precio(this.precio)
                .activo(this.activo != null ? this.activo : true)
                .build();
    }
}
