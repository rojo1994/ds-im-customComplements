package com.coppel.client.customcomplements.dto;

import com.coppel.client.customcomplements.dto.PsMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Bloqueo del código (antes proc_mxtrf010bloqueos con la IP del equipo; ahora el número de empleado).
 */
public record LockRequest(
    @NotNull(message = PsMessages.AREA_REQUERIDA) CustomsArea area,
    @Positive(message = PsMessages.CODIGO_NO_VALIDO) @Max(value = 999999, message = PsMessages.CODIGO_NO_VALIDO) int code,
    @Positive(message = PsMessages.EMPLEADO_REQUERIDO) long employee
) {
}
