package com.coppel.client.customcomplements.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * proc_mxtrf010validarimpi / proc_mxtrf010eliminardatosimpi. Por marca ({@code piece} = 0, con
 * {@code brandId}) o por pieza (pieza, rango y fracción).
 */
public record ImpiRequest(
    @NotNull @Size(max = 22) String brandId,
    @PositiveOrZero int piece,
    @PositiveOrZero int sizeRange,
    @NotNull @Size(max = 22) String fraction
) {
}
