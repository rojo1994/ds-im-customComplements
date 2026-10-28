package com.coppel.dto.request.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Fracción MXN capturada en Información aduanal.
 *
 * @param previousFraction fracción que tenía la pieza (para preguntar por el IMPI si cambia)
 */
public record FractionCheckRequest(
    @NotNull(message = AppMessages.AREA_REQUERIDA) CustomsArea area,
    @Positive(message = AppMessages.CODIGO_6_DIGITOS) int code,
    @Positive int piece,
    @PositiveOrZero int sizeRange,
    String fraction,
    String previousFraction,
    Integer countryId
) {
}
