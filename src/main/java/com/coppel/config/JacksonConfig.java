package com.coppel.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;

/**
 * Jackson 3 (Spring Boot 4) rechaza por omisión un campo primitivo ausente o nulo. El {@code MetaDTO} común se
 * serializa con {@code NON_DEFAULT} (ps no manda {@code rollback = false}) y el front puede omitir banderas en falso:
 * ausente vale el valor por omisión del tipo (false / 0), como con Jackson 2.
 */
@Configuration
public class JacksonConfig {

    @Bean
    JsonMapperBuilderCustomizer primitivesDefaultCustomizer() {
        return builder -> builder.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    }
}
