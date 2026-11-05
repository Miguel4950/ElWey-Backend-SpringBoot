package com.elwey.restaurante.repository;

import com.elwey.restaurante.entities.Comida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComidaRepository extends JpaRepository<Comida, Long> {
    List<Comida> findByNombre(String nombre);
    List<Comida> findByNombreContainingIgnoreCase(String nombre);
    List<Comida> findByNombreContainingIgnoreCaseOrCategoriaNombreContainingIgnoreCase(String nombre, String categoriaNombre);
    List<Comida> findByActivoTrue();

    // Consultas personalizadas con @Query creadas para pruebas (Semana 13)
    @Query("SELECT c FROM Comida c WHERE c.precio >= :precioMinimo AND c.activo = true")
    List<Comida> findComidasConPrecioMayorOIgual(@Param("precioMinimo") Double precioMinimo);

    @Query("SELECT c FROM Comida c WHERE LOWER(c.categoria.nombre) = LOWER(:categoriaNombre) AND LOWER(c.etiqueta) = LOWER(:etiqueta)")
    List<Comida> findComidasPorCategoriaYEtiqueta(@Param("categoriaNombre") String categoriaNombre, @Param("etiqueta") String etiqueta);

    @Query("SELECT DISTINCT c.etiqueta FROM Comida c WHERE c.etiqueta IS NOT NULL AND TRIM(c.etiqueta) <> '' AND c.activo = true ORDER BY c.etiqueta ASC")
    List<String> findDistinctEtiquetasActivas();
}
