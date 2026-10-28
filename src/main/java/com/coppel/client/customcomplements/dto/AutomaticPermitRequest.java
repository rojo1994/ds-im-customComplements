package com.coppel.client.customcomplements.dto;

import com.coppel.client.customcomplements.dto.PsMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Parámetros de dbo.fun_validapermisoautomatico (antes AduanasDAL.ValidarPermisoAutomatico).
 */
public record AutomaticPermitRequest(
    @Positive(message = PsMessages.CODIGO_NO_VALIDO) int code,
    @NotBlank(message = PsMessages.FRACCION_REQUERIDA) String fraction,
    int commercialUnitId,
    @NotNull BigDecimal unitCost,
    @NotNull BigDecimal tariffValue,
    int pieces
) {
}
