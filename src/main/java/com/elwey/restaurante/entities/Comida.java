package com.elwey.restaurante.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un plato de comida en el restaurante.
 * Posee relación ManyToOne con Categoria y OneToMany con ItemPedido.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"categoria", "itemsPedido"})
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(nullable = false, unique = false)
    private Double precio;

    @Column(nullable = true, unique = false, length = 255)
    private String descripcion;

    @Column(name = "imagen_url", nullable = true, unique = false, length = 500)
    private String imagenUrl;

    @Column(nullable = true, unique = false, length = 50)
    private String etiqueta;

    @Column(nullable = false, unique = false)
    @Builder.Default
    private Boolean activo = true;

    // Relación Many-to-One: la comida es dueña de la llave foránea con Categoria
    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false, unique = false)
    private Categoria categoria;

    // Relación One-to-Many con ItemPedido
    @JsonIgnore
    @OneToMany(mappedBy = "comida")
    @Builder.Default
    private List<ItemPedido> itemsPedido = new ArrayList<>();

    // Constructor de compatibilidad para inicializaciones del DataLoader
    public Comida(Long id, String nombre, Double precio, String descripcion, String imagenUrl, String etiqueta, Boolean activo, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.etiqueta = etiqueta;
        this.activo = activo != null ? activo : true;
        this.categoria = categoria;
        this.itemsPedido = new ArrayList<>();
    }

    // Métodos de compatibilidad con las vistas de plantillas
    @JsonIgnore
    public Comida getComida() {
        return this;
    }

    public String getNombreCategoria() {
        return this.categoria != null ? this.categoria.getNombre() : "Sin Categoría";
    }
}
