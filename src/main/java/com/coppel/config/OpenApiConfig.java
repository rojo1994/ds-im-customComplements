package com.coppel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customComplementsOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("ds-im-customComplements")
                .description("Negocio de Complementos aduana (migración de MXTRF010): casos de uso de la pantalla y de sus "
                    + "subformularios con los mensajes del escritorio (v1/custom-complements). Persiste con "
                    + "ps-im-customComplements.")
                .version("1.0.0"));
    }
}
