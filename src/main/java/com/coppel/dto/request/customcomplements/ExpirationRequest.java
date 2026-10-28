package com.coppel.dto.request.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.ExpirationDocument;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Fecha a validar con dbo.fun_validarfechavencimiento.
 */
public record ExpirationRequest(
    @NotNull(message = AppMessages.FECHA_NO_VALIDA) LocalDate date,
    @NotNull ExpirationDocument document
) {
}
