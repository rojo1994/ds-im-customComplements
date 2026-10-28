package com.coppel.dto.customcomplements;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * Renglón de {@code tmp_ieps<área><código>}. Se lee con {@code proc_mxtrf010obtenerdatosipes} y se escribe con
 * {@code proc_mxtrf010actualizartmpieps}.
 */
public record IepsRowDTO(
    @PositiveOrZero int piece,
    @PositiveOrZero int sizeRange,
    int classId,
    int categoryId,
    @NotNull BigDecimal categoryPercentage,
    @NotNull BigDecimal additionalCost,
    int unitId,
    @NotNull BigDecimal quantity
) {
}
