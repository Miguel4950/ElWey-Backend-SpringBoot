package com.elwey.restaurante.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración central de OpenAPI / Swagger para documentar los endpoints REST del Restaurante El Wey.
 * Disponible en: http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Restaurante El Wey - Sprint 10 (Entrega 2)")
                        .version("2.0.0")
                        .description("Documentación interactiva de la API REST para el restaurante de comida mexicana 'El Wey'. " +
                                "Proporciona servicios para gestión de comidas, adicionales, pedidos, domiciliarios, clientes y operadores.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo El Wey")
                                .email("soporte@elwey.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
