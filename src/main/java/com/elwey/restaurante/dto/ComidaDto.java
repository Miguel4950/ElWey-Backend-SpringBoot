package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.Adicional;
import com.elwey.restaurante.entities.Comida;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para transferencia de datos de Comida hacia y desde el Frontend.
 * Envía la información del plato, su categoría y los adicionales asociados en el DTO
 * sin almacenarlos directamente en la entidad Comida.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComidaDto {

    private Long id;
    private String nombre;
    private Double precio;
    private String descripcion;

    @JsonProperty("imagen_url")
    @JsonAlias({ "imagenUrl", "imagen_url" })
    private String imagenUrl;

    private String etiqueta;
    private Boolean activo;

    private Long categoriaId;
    private String categoriaNombre;

    @Builder.Default
    private List<Adicional> adicionales = new ArrayList<>();

    @Builder.Default
    private List<Long> adicionalesIds = new ArrayList<>();

    public static ComidaDto fromEntity(Comida comida) {
        if (comida == null) return null;
        Long catId = (comida.getCategoria() != null) ? comida.getCategoria().getId() : null;
        String catNombre = (comida.getCategoria() != null) ? comida.getCategoria().getNombre() : null;

        List<Adicional> listaAdicionales = new ArrayList<>();
        List<Long> adicIds = new ArrayList<>();
        if (comida.getCategoria() != null && comida.getCategoria().getAdicionales() != null) {
            listaAdicionales = new ArrayList<>(comida.getCategoria().getAdicionales());
            for (Adicional a : listaAdicionales) {
                if (a != null && a.getId() != null) {
                    adicIds.add(a.getId());
                }
            }
        }

        return ComidaDto.builder()
                .id(comida.getId())
                .nombre(comida.getNombre())
                .precio(comida.getPrecio())
                .descripcion(comida.getDescripcion())
                .imagenUrl(comida.getImagenUrl())
                .etiqueta(comida.getEtiqueta())
                .activo(comida.getActivo())
                .categoriaId(catId)
                .categoriaNombre(catNombre)
                .adicionales(listaAdicionales)
                .adicionalesIds(adicIds)
                .build();
    }

    public static ComidaDto fromEntityWithAdicionales(Comida comida, List<Adicional> adicionales) {
        ComidaDto dto = fromEntity(comida);
        if (dto != null && adicionales != null) {
            dto.setAdicionales(adicionales);
            List<Long> ids = new ArrayList<>();
            for (Adicional a : adicionales) {
                if (a != null && a.getId() != null) {
                    ids.add(a.getId());
                }
            }
            dto.setAdicionalesIds(ids);
        }
        return dto;
    }

    public Comida toEntity() {
        return Comida.builder()
                .id(this.id)
                .nombre(this.nombre)
                .precio(this.precio)
                .descripcion(this.descripcion)
                .imagenUrl(this.imagenUrl)
                .etiqueta(this.etiqueta)
                .activo(this.activo != null ? this.activo : true)
                .build();
    }
}
