package com.coppel.dto.response.customcomplements;

import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.constants.customcomplements.CustomsArea;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Parte superior de FrmComplementosAduanales.
 *
 * @param brandId            marca capturada; si el código es nuevo, la de compras (MAC) cuando está en el combo
 * @param purchaseBrand      MAC: marca en compras
 * @param purchaseModel      MOC: modelo en compras
 * @param componentCount     txtNumeroComponentes (null si no es combinado)
 * @param generalCost        costo general del código (Muebles ya dividido entre 100)
 * @param sizeRange          tallas del artículo "mín-máx" (Ropa)
 * @param registered         el código ya está en ctl_complementosgeneral
 * @param purchaseFraction   Fracción FRC del maestro de compras (sólo Muebles; null en Ropa)
 */
public record CodeHeaderDTO(
    CustomsArea area,
    int code,
    int supplierId,
    String supplierName,
    Integer importerId,
    List<CatalogItemDTO> brands,
    Integer brandId,
    int brandTypeId,
    String brandType,
    String model,
    String purchaseBrand,
    String purchaseModel,
    boolean ftz,
    boolean pendingLabel,
    boolean brandImage,
    LocalDate brandImageExpiration,
    boolean sanitaryNoticeInProcess,
    boolean combined,
    Integer componentCount,
    String generalDescription,
    BigDecimal generalCost,
    int representativePiece,
    int representativeRange,
    String sizeRange,
    boolean registered,
    String purchaseFraction
) {
}
