package com.coppel.client.customcomplements.dto;

/**
 * Resultado del alta de marca en ps ({@code brandId} null cuando {@code duplicated}).
 */
public record BrandCreatedDTO(
    Integer brandId,
    boolean duplicated
) {
}
