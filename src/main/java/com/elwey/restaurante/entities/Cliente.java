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
@ToString(exclude = "pedidos")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = false, length = 70)
    private String nombre;

    @Column(nullable = true, unique = false, length = 70)
    private String apellido;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = false, length = 20)
    private String telefono;

    @Column(nullable = true, unique = false, length = 100)
    private String password;

    @Column(nullable = true, unique = false, length = 150)
    private String direccion;

    @Column(nullable = true, unique = false, length = 30)
    private String fechaRegistro;

    @Column(nullable = false, unique = false)
    @Builder.Default
    private Boolean activo = true;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente")
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();

    // Métodos de compatibilidad con vistas y formularios anteriores y diagrama ER
    public String getCorreo() {
        return this.email;
    }

    public void setCorreo(String correo) {
        this.email = correo;
    }

    public String getContrasena() {
        return this.password;
    }

    public void setContrasena(String contrasena) {
        this.password = contrasena;
    }

    public boolean getActivo() {
        return this.activo;
    }

    public String getNombreCompleto() {
        if (this.apellido != null && !this.apellido.isBlank()) {
            return this.nombre + " " + this.apellido;
        }
        return this.nombre;
    }
}
