package com.coppel.client.customcomplements.dto;

/**
 * Talla mínima y máxima del artículo (proc_mxtrf010obtenertallasarticulo).
 */
public record SizeLimitsDTO(
    Integer minSize,
    Integer maxSize
) {
}
