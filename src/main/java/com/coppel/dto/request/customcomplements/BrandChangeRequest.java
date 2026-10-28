package com.coppel.dto.request.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Cambio de marca (cbMarca_SelectionChangeCommitted).
 */
public record BrandChangeRequest(
    @NotNull(message = AppMessages.AREA_REQUERIDA) CustomsArea area,
    @Positive(message = AppMessages.CODIGO_6_DIGITOS) int code,
    Integer previousBrandId,
    @Positive(message = AppMessages.SELECCIONAR_MARCA) int brandId
) {
}
