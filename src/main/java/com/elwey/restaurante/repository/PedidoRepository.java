package com.elwey.restaurante.repository;

import com.elwey.restaurante.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByEstado(String estado);
    List<Pedido> findByDomiciliarioId(Long domiciliarioId);

    // Consultas personalizadas con @Query creadas para pruebas (Semana 13)
    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado AND p.domiciliario.id = :domiciliarioId")
    List<Pedido> findPedidosPorEstadoYDomiciliario(@Param("estado") String estado, @Param("domiciliarioId") Long domiciliarioId);

    @Query("SELECT p FROM Pedido p WHERE LOWER(p.cliente.email) = LOWER(:email)")
    List<Pedido> findPedidosPorClienteEmail(@Param("email") String email);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long countPedidosPorEstado(@Param("estado") String estado);
}
