package com.coppel.dto.request.customcomplements;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

/**
 * Aceptar de FrmAvisoSanitario: el aviso se graba con la pieza / rango representativos y la fracción que lleva aviso
 * (antes ActualizarRegulaciones).
 */
public record SanitaryNoticeRequest(
    @PositiveOrZero int representativePiece,
    @PositiveOrZero int representativeRange,
    String fraction,
    List<@Valid SanitaryOfficeInput> offices
) {
    public SanitaryNoticeRequest {
        offices = offices == null ? List.of() : List.copyOf(offices);
    }
}
