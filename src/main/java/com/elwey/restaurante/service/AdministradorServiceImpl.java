package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.AdministradorDto;
import com.elwey.restaurante.entities.Administrador;
import com.elwey.restaurante.errors.CredencialesInvalidasException;
import com.elwey.restaurante.repository.AdministradorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de negocio para la autenticación de Administrador.
 */
@Slf4j
@Service
public class AdministradorServiceImpl implements AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepo;

    @Override
    @Transactional(readOnly = true)
    public AdministradorDto autenticar(String usuario, String contrasena) {
        log.info("Autenticando administrador por usuario: {}", usuario);

        if (usuario == null || usuario.trim().isEmpty() || contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar usuario y contraseña.");
        }

        String limpio = usuario.trim().toLowerCase();

        Administrador administrador = administradorRepo.findByUsuario(limpio).orElse(null);
        if (administrador != null && contrasena.equals(administrador.getContrasena())) {
            return AdministradorDto.fromEntity(administrador);
        } else {
            throw new CredencialesInvalidasException("Credenciales inválidas. Verifica tu usuario y contraseña.");
        }
    }
}
