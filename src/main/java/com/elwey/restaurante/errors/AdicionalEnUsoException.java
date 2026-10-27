package com.elwey.restaurante.errors;

public class AdicionalEnUsoException extends RuntimeException {
    public AdicionalEnUsoException(Long id, String nombre) {
        super("No se puede eliminar el adicional '" + nombre + "' (ID: " + id
                + ") porque está asignado a uno o más pedidos.");
    }
}
