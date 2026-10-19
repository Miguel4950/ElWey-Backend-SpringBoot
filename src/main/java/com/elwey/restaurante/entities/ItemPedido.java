package com.elwey.restaurante.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"pedido", "comida", "adicionales"})
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = false)
    private Integer cantidad;

    @Transient
    private Double subtotal;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false, unique = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_comida", nullable = false, unique = false)
    private Comida comida;

    @OneToMany(mappedBy = "itemPedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Builder.Default
    private List<ItemPedidoAdicional> adicionales = new ArrayList<>();
}
