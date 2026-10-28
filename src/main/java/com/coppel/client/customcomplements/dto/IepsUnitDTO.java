package com.coppel.client.customcomplements.dto;

/**
 * Unidad de medida IEPS (proc_mxtrf010obtenerunidadmedidaieps).
 *
 * @param decimals num_decimalesiesp: la cantidad admite decimales (PIEZA no)
 */
public record IepsUnitDTO(
    int id,
    String name,
    boolean decimals
) {
}
