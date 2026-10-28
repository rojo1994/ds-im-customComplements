package com.coppel.client.customcomplements.dto;

import com.coppel.constants.customcomplements.CustomsArea;

/**
 * Alta de marca en ps (Catalogos.dbo.cat_marcas) para el proveedor del código.
 */
public record BrandRequest(
    CustomsArea area,
    int supplierId,
    int brandTypeId,
    String description
) {
}
