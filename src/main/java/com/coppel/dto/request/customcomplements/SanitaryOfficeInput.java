package com.coppel.dto.request.customcomplements;

import java.time.LocalDate;

/**
 * Renglón de dgvAvisoSanitario.
 */
public record SanitaryOfficeInput(
    int officeId,
    String office,
    boolean applies,
    String folio,
    LocalDate validity,
    Integer exceptionId
) {
}
