package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class ReporteServiceImpl implements ReporteService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SEPARADOR = ";";

    @Autowired
    private PedidoService pedidoService;

    @Override
    @Transactional(readOnly = true)
    public byte[] generarReportePedidosCsv() {
        log.info("Generando reporte CSV consolidado de pedidos para administración...");
        List<Pedido> pedidos = pedidoService.searchAll();

        StringBuilder sb = new StringBuilder();
        // UTF-8 BOM para que Excel en Windows reconozca tildes y caracteres especiales automáticamente
        sb.append('\uFEFF');

        // Cabeceras del reporte
        sb.append("ID Pedido").append(SEPARADOR)
          .append("Fecha Creacion").append(SEPARADOR)
          .append("Estado").append(SEPARADOR)
          .append("Cliente").append(SEPARADOR)
          .append("Telefono Cliente").append(SEPARADOR)
          .append("Direccion Entrega").append(SEPARADOR)
          .append("Cantidad Platos").append(SEPARADOR)
          .append("Cantidad Adicionales").append(SEPARADOR)
          .append("Subtotal Comidas (COP)").append(SEPARADOR)
          .append("Subtotal Adicionales (COP)").append(SEPARADOR)
          .append("Total (COP)").append(SEPARADOR)
          .append("Domiciliario Asignado").append("\n");

        if (pedidos != null) {
            for (Pedido p : pedidos) {
                // Asegurar cálculo de campos contables @Transient
                pedidoService.calcularTotales(p);

                String id = p.getId() != null ? String.valueOf(p.getId()) : "";
                String fecha = p.getFechaCreacion() != null ? p.getFechaCreacion().format(FORMATTER) : "N/A";
                String estado = p.getEstado() != null ? p.getEstado() : "PENDIENTE";
                String clienteNombre = p.getCliente() != null && p.getCliente().getNombre() != null ? p.getCliente().getNombre() : "N/A";
                String clienteTel = p.getCliente() != null && p.getCliente().getTelefono() != null ? p.getCliente().getTelefono() : "N/A";
                String clienteDir = p.getCliente() != null && p.getCliente().getDireccion() != null ? p.getCliente().getDireccion() : "A coordinar";

                int cantPlatos = p.getCantidadTotalProductos() != null ? p.getCantidadTotalProductos() : 0;
                int cantAdicionales = p.getCantidadTotalAdicionales() != null ? p.getCantidadTotalAdicionales() : 0;
                double subComida = p.getSubtotalComida() != null ? p.getSubtotalComida() : 0.0;
                double subAdicionales = p.getSubtotalAdicionales() != null ? p.getSubtotalAdicionales() : 0.0;
                double total = p.getTotal() != null ? p.getTotal() : 0.0;

                String domiciliario = p.getDomiciliario() != null && p.getDomiciliario().getNombre() != null
                        ? p.getDomiciliario().getNombre()
                        : "Sin asignar";

                sb.append(escaparCsv(id)).append(SEPARADOR)
                  .append(escaparCsv(fecha)).append(SEPARADOR)
                  .append(escaparCsv(estado)).append(SEPARADOR)
                  .append(escaparCsv(clienteNombre)).append(SEPARADOR)
                  .append(escaparCsv(clienteTel)).append(SEPARADOR)
                  .append(escaparCsv(clienteDir)).append(SEPARADOR)
                  .append(cantPlatos).append(SEPARADOR)
                  .append(cantAdicionales).append(SEPARADOR)
                  .append(Math.round(subComida)).append(SEPARADOR)
                  .append(Math.round(subAdicionales)).append(SEPARADOR)
                  .append(Math.round(total)).append(SEPARADOR)
                  .append(escaparCsv(domiciliario)).append("\n");
            }
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escaparCsv(String valor) {
        if (valor == null) return "";
        if (valor.contains(SEPARADOR) || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
