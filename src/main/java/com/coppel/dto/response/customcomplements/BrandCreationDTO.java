package com.coppel.dto.response.customcomplements;

import com.coppel.client.customcomplements.dto.CatalogItemDTO;

import java.util.List;

/**
 * Marca dada de alta y el combo de marcas del proveedor ya actualizado.
 */
public record BrandCreationDTO(
    int brandId,
    List<CatalogItemDTO> brands
) {
}
