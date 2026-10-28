package com.coppel.client.customcomplements.dto;

import java.math.BigDecimal;

/**
 * Categoría IEPS de una clase (proc_mxtrf010obtenercategoriasieps).
 */
public record IepsCategoryDTO(
    int id,
    String name,
    BigDecimal percentage,
    BigDecimal additionalCost,
    int unitId
) {
}
