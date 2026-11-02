package com.elwey.restaurante.errors;

public class DomiciliarioConPedidoEnviadoException extends RuntimeException {
    public DomiciliarioConPedidoEnviadoException(String nombre) {
        super("No se puede marcar como libre/disponible al domiciliario '" + nombre + "' porque actualmente tiene pedidos en estado Enviado (en ruta de despacho).");
    }
}
