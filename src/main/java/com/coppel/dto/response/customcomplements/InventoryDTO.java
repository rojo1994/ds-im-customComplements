package com.coppel.dto.response.customcomplements;

import java.math.BigDecimal;

/**
 * Renglón de dgvInventario ya convertido (Muebles: CostoDolares / 100; Ropa: importe / existencia truncado a 2).
 */
public record InventoryDTO(
    String warehouse,
    long quantity,
    BigDecimal cost
) {
}
