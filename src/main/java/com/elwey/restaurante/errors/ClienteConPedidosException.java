package com.elwey.restaurante.errors;

public class ClienteConPedidosException extends RuntimeException {
    public ClienteConPedidosException(String nombre) {
        super("No se puede eliminar el cliente '" + nombre + "' porque tiene pedidos registrados en el restaurante.");
    }
}

