package com.elwey.restaurante.dto;

import com.elwey.restaurante.entities.Comida;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * DTO plano para transferencia de datos de Comida hacia y desde el Frontend.
 * Envía únicamente la información del plato y su categoría.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComidaDto {

    private Long id;

    @NotBlank(message = "El nombre del plato no puede estar vacío")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un número positivo mayor a cero")
    private Double precio;

    private String descripcion;

    @JsonProperty("imagen_url")
    @JsonAlias({ "imagenUrl", "imagen_url" })
    private String imagenUrl;

    private String etiqueta;
    private Boolean activo;

    private Long categoriaId;
    private String categoriaNombre;

    public static ComidaDto fromEntity(Comida comida) {
        if (comida == null) return null;
        Long catId = (comida.getCategoria() != null) ? comida.getCategoria().getId() : null;
        String catNombre = (comida.getCategoria() != null) ? comida.getCategoria().getNombre() : null;

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
                .build();
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

    public static Comida toEntity(ComidaDto dto) {
        if (dto == null) return null;
        return dto.toEntity();
    }
}
