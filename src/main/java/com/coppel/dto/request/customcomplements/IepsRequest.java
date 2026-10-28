package com.coppel.dto.request.customcomplements;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * Aceptar de FrmIEPS.
 */
public record IepsRequest(
    @PositiveOrZero int piece,
    @PositiveOrZero int sizeRange,
    Integer classId,
    Integer categoryId,
    Integer unitId,
    BigDecimal quantity
) {
}
