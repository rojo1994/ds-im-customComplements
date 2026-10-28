package com.coppel.dto.response.customcomplements;

import com.coppel.constants.customcomplements.ExpirationStatus;

/**
 * Resultado de dbo.fun_validarfechavencimiento: VALID (blanco), EXPIRING (naranja) o EXPIRED (rojo) y el mensaje.
 */
public record ExpirationStatusDTO(
    ExpirationStatus status,
    String message
) {
    public static ExpirationStatusDTO valid() {
        return new ExpirationStatusDTO(ExpirationStatus.VALID, "");
    }
}
