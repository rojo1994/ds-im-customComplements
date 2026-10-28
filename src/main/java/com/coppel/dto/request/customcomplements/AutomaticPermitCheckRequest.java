package com.coppel.dto.request.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Datos con los que FrmAduanas recalculaba el permiso automático (ValidarPermisoAutomatico).
 */
public record AutomaticPermitCheckRequest(
    @Positive(message = AppMessages.CODIGO_6_DIGITOS) int code,
    String fraction,
    Integer commercialUnitId,
    BigDecimal unitCost,
    BigDecimal tariffValue,
    Integer pieces
) {
}
