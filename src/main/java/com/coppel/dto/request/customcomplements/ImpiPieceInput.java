package com.coppel.dto.request.customcomplements;

/**
 * Pieza cuya fracción cambió con datos de IMPI aceptados para borrar (antes GridComponente.CambioImpi).
 */
public record ImpiPieceInput(
    int piece,
    int sizeRange,
    String previousFraction
) {
}
