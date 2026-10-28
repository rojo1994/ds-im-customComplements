package com.coppel.constants.customcomplements;

/**
 * Color con el que el escritorio pintaba una fecha según dbo.fun_validarfechavencimiento: blanco (vigente), naranja
 * ("Está por vencer…") o rojo ("… está vencido.").
 */
public enum ExpirationStatus {
    VALID,
    EXPIRING,
    EXPIRED
}
