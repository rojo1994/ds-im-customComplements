package com.coppel.dto.request.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Aceptar de Información aduanal: el renglón capturado de la pieza / rango.
 */
public record CustomsInfoRequest(
    @NotNull(message = AppMessages.AREA_REQUERIDA) CustomsArea area,
    @Positive(message = AppMessages.CODIGO_6_DIGITOS) int code,
    @NotNull CustomsRowDTO row
) {
}
