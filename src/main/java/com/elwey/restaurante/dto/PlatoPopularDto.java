package com.elwey.restaurante.dto;

/**
 * DTO para representar el rendimiento y popularidad de un plato en las métricas gastronómicas.
 */
public class PlatoPopularDto {

    private String nombre;
    private String categoria;
    private Integer cantidadVendida;
    private Double porcentaje;

    public PlatoPopularDto() {
    }

    public PlatoPopularDto(String nombre, String categoria, Integer cantidadVendida, Double porcentaje) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidadVendida = cantidadVendida;
        this.porcentaje = porcentaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Integer cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public Double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Double porcentaje) {
        this.porcentaje = porcentaje;
    }
}
