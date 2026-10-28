package com.coppel.dto.request.customcomplements;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Parte superior de la pantalla al pulsar Guardar.
 */
public record SaveHeaderInput(
    int supplierId,
    Integer importerId,
    Integer brandId,
    int brandTypeId,
    String model,
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
    int representativeRange
) {
}
