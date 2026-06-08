package com.brujabeer.recetario.config;

import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración Spring extra.
 * Por ahora vacía: Spring Boot autoconfigura JPA, HikariCP y el contexto.
 *
 * Aquí pondrás en el futuro:
 *   - @Bean de conversores de tipo personalizados
 *   - Configuración de caché
 *   - Cualquier bean que no sea componente escaneable
 */
@Configuration
public class AppConfig {
    // Spring Boot autoconfigures everything from application.properties
}
