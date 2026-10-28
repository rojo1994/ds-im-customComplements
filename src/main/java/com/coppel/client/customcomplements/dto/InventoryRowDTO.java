package com.coppel.client.customcomplements.dto;

import java.math.BigDecimal;

/**
 * Renglón de proc_mxtrf010obtenerinventario tal como lo regresa el SP.
 *
 * @param warehouse Siglas de la bodega
 * @param quantity  Existencia (Muebles) / ExisUnidades (Ropa)
 * @param amount    CostoDolares en centavos (Muebles) / ExisPesos, importe total (Ropa)
 */
public record InventoryRowDTO(
    String warehouse,
    long quantity,
    BigDecimal amount
) {
}
