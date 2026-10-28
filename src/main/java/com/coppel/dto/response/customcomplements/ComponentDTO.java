package com.coppel.dto.response.customcomplements;

/**
 * Renglón de dgvComponentes: pieza y rango de tallas (Muebles siempre 0). Lo demás del renglón (fracción, %, PE,
 * costo, color, botones) se calcula en el front desde la información aduanal del Store.
 *
 * @param sizes          texto de la columna Rango de tallas ("22-26" o una sola talla)
 * @param representative CP: pieza / rango representativo del código
 */
public record ComponentDTO(
    int piece,
    int sizeRange,
    String sizes,
    boolean representative
) {
}
