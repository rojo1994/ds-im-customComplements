package com.coppel.dto.request.customcomplements;

import com.coppel.dto.customcomplements.SizeRowDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

/**
 * Aceptar de FrmRangoDeTallas: las tallas de todas las piezas con su rango y el representativo actual (se conserva si
 * su pieza / rango sigue existiendo).
 */
public record SizeRangesRequest(
    @NotEmpty List<@Valid SizeRowDTO> sizes,
    @PositiveOrZero int representativePiece,
    @PositiveOrZero int representativeRange
) {
}
