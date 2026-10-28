package com.coppel.client.customcomplements.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resultado de proc_mxtrf010obtenerdatoscodigo. Si el código no está en ctl_complementosgeneral el SP sólo regresa
 * importadora, marca y modelo de compras, FTZ y costo ({@code registered = false}).
 *
 * @param purchaseBrand MAC: marca en compras
 * @param purchaseModel MOC: modelo en compras
 * @param model         des_modelo capturado (o el modelo de compras si no está registrado)
 * @param interiorCost  CostoInteriorDL (Muebles en centavos) / CostoUnitario de la talla (Ropa)
 */
public record CodeGeneralDTO(
    boolean registered,
    int importerId,
    Integer brandId,
    String purchaseBrand,
    String brandType,
    String model,
    String purchaseModel,
    boolean ftz,
    boolean brandImage,
    LocalDate brandImageExpiration,
    boolean pendingLabel,
    boolean sanitaryNoticeInProcess,
    boolean combined,
    String generalDescription,
    int representativePiece,
    int representativeRange,
    BigDecimal interiorCost
) {
}
