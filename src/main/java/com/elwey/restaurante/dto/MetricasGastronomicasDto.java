package com.elwey.restaurante.dto;

import java.util.List;

/**
 * DTO que consolida los KPIs del negocio y estadísticas culinarias en vivo del restaurante "El Wey".
 */
public class MetricasGastronomicasDto {

    private Long totalPlatosActivos;
    private Long totalPedidosCompletados;
    private Double ticketPromedio;
    private String platoEstrella;
    private String categoriaMasVendida;
    private Double indiceSatisfaccionCliente;
    private Integer tiempoEstimadoCocinaMinutos;
    private List<PlatoPopularDto> platosPopulares;

    public MetricasGastronomicasDto() {
    }

    public MetricasGastronomicasDto(Long totalPlatosActivos, Long totalPedidosCompletados,
                                    Double ticketPromedio, String platoEstrella,
                                    String categoriaMasVendida, Double indiceSatisfaccionCliente,
                                    Integer tiempoEstimadoCocinaMinutos, List<PlatoPopularDto> platosPopulares) {
        this.totalPlatosActivos = totalPlatosActivos;
        this.totalPedidosCompletados = totalPedidosCompletados;
        this.ticketPromedio = ticketPromedio;
        this.platoEstrella = platoEstrella;
        this.categoriaMasVendida = categoriaMasVendida;
        this.indiceSatisfaccionCliente = indiceSatisfaccionCliente;
        this.tiempoEstimadoCocinaMinutos = tiempoEstimadoCocinaMinutos;
        this.platosPopulares = platosPopulares;
    }

    public Long getTotalPlatosActivos() {
        return totalPlatosActivos;
    }

    public void setTotalPlatosActivos(Long totalPlatosActivos) {
        this.totalPlatosActivos = totalPlatosActivos;
    }

    public Long getTotalPedidosCompletados() {
        return totalPedidosCompletados;
    }

    public void setTotalPedidosCompletados(Long totalPedidosCompletados) {
        this.totalPedidosCompletados = totalPedidosCompletados;
    }

    public Double getTicketPromedio() {
        return ticketPromedio;
    }

    public void setTicketPromedio(Double ticketPromedio) {
        this.ticketPromedio = ticketPromedio;
    }

    public String getPlatoEstrella() {
        return platoEstrella;
    }

    public void setPlatoEstrella(String platoEstrella) {
        this.platoEstrella = platoEstrella;
    }

    public String getCategoriaMasVendida() {
        return categoriaMasVendida;
    }

    public void setCategoriaMasVendida(String categoriaMasVendida) {
        this.categoriaMasVendida = categoriaMasVendida;
    }

    public Double getIndiceSatisfaccionCliente() {
        return indiceSatisfaccionCliente;
    }

    public void setIndiceSatisfaccionCliente(Double indiceSatisfaccionCliente) {
        this.indiceSatisfaccionCliente = indiceSatisfaccionCliente;
    }

    public Integer getTiempoEstimadoCocinaMinutos() {
        return tiempoEstimadoCocinaMinutos;
    }

    public void setTiempoEstimadoCocinaMinutos(Integer tiempoEstimadoCocinaMinutos) {
        this.tiempoEstimadoCocinaMinutos = tiempoEstimadoCocinaMinutos;
    }

    public List<PlatoPopularDto> getPlatosPopulares() {
        return platosPopulares;
    }

    public void setPlatosPopulares(List<PlatoPopularDto> platosPopulares) {
        this.platosPopulares = platosPopulares;
    }
}
