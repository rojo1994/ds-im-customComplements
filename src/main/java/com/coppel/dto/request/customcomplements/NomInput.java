package com.coppel.dto.request.customcomplements;

import java.time.LocalDate;

/**
 * Renglón de dtgvNomCumplimiento (4) o dtgvInfoComercial (5).
 */
public record NomInput(
    int nomId,
    boolean applies,
    String certificate,
    LocalDate validity,
    Integer exceptionId
) {
}
