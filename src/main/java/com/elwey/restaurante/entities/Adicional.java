package com.elwey.restaurante.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Adicional (ingrediente extra, salsa, complemento).
 * Mapea relaciones ManyToMany con Categoria y OneToMany con ItemPedidoAdicional.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"categorias", "itemAdicionales"})
public class Adicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = false, length = 80)
    private String nombre;

    @Column(nullable = false, unique = false)
    private Double precio;

    @Column(nullable = false, unique = false)
    @Builder.Default
    private Boolean activo = true;

    @JsonIgnore
    @ManyToMany(mappedBy = "adicionales")
    @Builder.Default
    private List<Categoria> categorias = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "adicional", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Builder.Default
    private List<ItemPedidoAdicional> itemAdicionales = new ArrayList<>();

    // Método de compatibilidad
    @JsonIgnore
    public Categoria getCategoria() {
        return (categorias != null && !categorias.isEmpty()) ? categorias.get(0) : null;
    }
}
