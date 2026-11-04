package com.elwey.restaurante.service;

import com.elwey.restaurante.dto.AdministradorDto;
import com.elwey.restaurante.dto.ResumenEstadisticasDto;
import com.elwey.restaurante.entities.Administrador;
import com.elwey.restaurante.entities.Pedido;
import com.elwey.restaurante.errors.CredencialesInvalidasException;
import com.elwey.restaurante.repository.AdministradorRepository;
import com.elwey.restaurante.repository.ComidaRepository;
import com.elwey.restaurante.repository.DomiciliarioRepository;
import com.elwey.restaurante.repository.PedidoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de negocio para Administrador.
 */
@Slf4j
@Service
public class AdministradorServiceImpl implements AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepo;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    @Autowired
    private PedidoService pedidoService;

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

    @Override
    @Transactional(readOnly = true)
    public ResumenEstadisticasDto obtenerResumenEstadisticas() {
        log.info("Calculando resumen consolidado de estadísticas operacionales");

        Long totalPedidos = pedidoRepository.count();
        Long pedidosCocinando = pedidoRepository.countPedidosPorEstado("Cocinando");
        Long pedidosListos = pedidoRepository.countPedidosPorEstado("Listo");
        Long pedidosEnviados = pedidoRepository.countPedidosPorEstado("Enviado");
        Long pedidosEntregados = pedidoRepository.countPedidosPorEstado("Entregado");

        List<Pedido> entregados = pedidoRepository.findByEstado("Entregado");
        double sumaVentas = 0.0;
        for (Pedido p : entregados) {
            pedidoService.calcularTotales(p);
            sumaVentas += (p.getTotal() != null) ? p.getTotal() : 0.0;
        }

        long totalComidasActivas = (long) comidaRepository.findByActivoTrue().size();
        long domiciliariosDisponibles = (long) domiciliarioRepository.findByDisponible(true).size();
        long totalDomiciliarios = domiciliarioRepository.count();

        return ResumenEstadisticasDto.builder()
                .totalPedidos(totalPedidos)
                .pedidosCocinando(pedidosCocinando)
                .pedidosListos(pedidosListos)
                .pedidosEnviados(pedidosEnviados)
                .pedidosEntregados(pedidosEntregados)
                .totalVentasEntregadas(sumaVentas)
                .totalComidasActivas(totalComidasActivas)
                .domiciliariosDisponibles(domiciliariosDisponibles)
                .totalDomiciliarios(totalDomiciliarios)
                .build();
    }
}
