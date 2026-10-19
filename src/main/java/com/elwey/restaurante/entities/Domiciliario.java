package com.elwey.restaurante.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"pedidos"})
public class Domiciliario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(nullable = false, unique = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = false, length = 20)
    private String celular;

    @Column(nullable = false, unique = false)
    private Boolean disponible;

    @JsonIgnore
    @OneToMany(mappedBy = "domiciliario")
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();
}
