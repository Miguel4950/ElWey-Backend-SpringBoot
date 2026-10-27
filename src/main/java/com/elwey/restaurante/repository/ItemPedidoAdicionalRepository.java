package com.elwey.restaurante.repository;

import com.elwey.restaurante.entities.ItemPedidoAdicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPedidoAdicionalRepository extends JpaRepository<ItemPedidoAdicional, Long> {
    boolean existsByAdicionalId(Long adicionalId);
}
