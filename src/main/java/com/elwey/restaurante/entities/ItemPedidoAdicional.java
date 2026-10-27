package com.elwey.restaurante.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_pedido_adicional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"itemPedido", "adicional"})
public class ItemPedidoAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private Integer cantidad;

    @Transient
    private Double subtotal;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_item", nullable = false)
    private ItemPedido itemPedido;

    @ManyToOne
    @JoinColumn(name = "id_adicional", nullable = false)
    private Adicional adicional;

    public String getNombre() {
        return this.adicional != null ? this.adicional.getNombre() : "";
    }

    public Double getPrecio() {
        return this.adicional != null ? this.adicional.getPrecio() : 0.0;
    }
}
