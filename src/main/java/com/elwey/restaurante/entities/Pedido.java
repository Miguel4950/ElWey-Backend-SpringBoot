package com.elwey.restaurante.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"cliente", "domiciliario", "items"})
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = false, length = 30)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false, unique = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_entrega", nullable = true, unique = false)
    private LocalDateTime fechaEntrega;

    @Transient
    private Integer cantidadTotalProductos;

    @Transient
    private Integer cantidadTotalAdicionales;

    @Transient
    private Double subtotalComida;

    @Transient
    private Double subtotalAdicionales;

    @Transient
    private Double total;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false, unique = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_domiciliario", nullable = true, unique = false)
    private Domiciliario domiciliario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPedido> items = new ArrayList<>();
}
