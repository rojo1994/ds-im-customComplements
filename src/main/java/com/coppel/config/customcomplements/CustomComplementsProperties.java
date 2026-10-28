package com.coppel.config.customcomplements;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

/**
 * Configuración de Complementos aduana ({@code app.custom-complements.*}).
 *
 * @param psUrl       URL base de ps-im-customComplements, con su context-path
 * @param timeZone    zona horaria con la que se decide "hoy" (fechas de vencimiento; antes la fecha del equipo)
 * @param readTimeout tiempo máximo de cada llamada a ps (el snapshot y el commit ejecutan varios SP)
 */
@ConfigurationProperties(prefix = "app.custom-complements")
public record CustomComplementsProperties(
    String psUrl,
    @DefaultValue("America/Tijuana") String timeZone,
    @DefaultValue("300s") Duration readTimeout
) {
}
