package com.coppel.client.customcomplements.dto;

/**
 * dbo.fun_validarfechavencimiento: vacío si la fecha está vigente; "… está vencido." o "Está por vencer …".
 */
public record ExpirationMessageDTO(
    String message
) {
}
