package com.coppel.dto.response.customcomplements;

/**
 * Lo que el escritorio consultaba de una fracción MXN (validarfraccionmxn, validarregulacion, validarregulacion 1 y
 * validarieps).
 *
 * @param appliesRegulations la fracción lleva regulaciones 2..5 (habilita el botón Regulaciones)
 * @param requiresIeps       habilita el botón IEPS
 * @param sanitaryNotice     lleva aviso sanitario (regulación 1)
 */
public record FractionRulesDTO(
    String fraction,
    boolean exists,
    boolean appliesDecree,
    int tariffUnitId,
    String tariffUnitName,
    boolean containsSugar,
    boolean appliesRegulations,
    boolean requiresIeps,
    boolean sanitaryNotice
) {
}
