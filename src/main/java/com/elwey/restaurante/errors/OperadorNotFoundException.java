package com.elwey.restaurante.errors;

public class OperadorNotFoundException extends RuntimeException {
    public OperadorNotFoundException(Long id) {
        super("Operador no encontrado con ID: " + id);
    }

    public OperadorNotFoundException(String usuario) {
        super("Operador no encontrado con usuario: " + usuario);
    }
}
