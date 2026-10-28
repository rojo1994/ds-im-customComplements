package com.coppel.client.customcomplements.dto;

/**
 * Opción de un combo.
 *
 * @param id   valor que viaja en la petición
 * @param name texto visible
 */
public record CatalogItemDTO(
    String id,
    String name
) {
}
