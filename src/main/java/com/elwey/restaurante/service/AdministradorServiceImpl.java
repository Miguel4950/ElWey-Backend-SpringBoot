package com.elwey.restaurante.service;

import com.elwey.restaurante.entities.Administrador;
import com.elwey.restaurante.errors.CredencialesInvalidasException;
import com.elwey.restaurante.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    @Transactional(readOnly = true)
    public String autenticar(String usuario, String contrasena) {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("Debe proporcionar usuario y contraseña.");
        }

        Administrador administrador = administradorRepository.findByUsuario(usuario.trim().toLowerCase())
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario administrador no encontrado."));

        if (administrador.getContrasena() == null || !contrasena.equals(administrador.getContrasena())) {
            throw new CredencialesInvalidasException("Revisar credenciales.");
        }

        return administrador.getUsuario();
    }
}
