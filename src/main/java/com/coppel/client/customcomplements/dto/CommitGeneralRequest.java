package com.coppel.client.customcomplements.dto;

import com.coppel.client.customcomplements.dto.PsMessages;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Parámetros de proc_mxtrf010grabarcomplementos opción 0 (ctl_complementosgeneral). {@code @nomtramite} siempre
 * viaja en 0, como el escritorio.
 *
 * @param componentPieces       suma de las piezas que componen cada componente
 * @param compensatoryQuota     suma de la cuota compensatoria de los componentes, truncada a 2 decimales
 * @param requiresSanitaryNotice alguna fracción lleva aviso sanitario (regulación 1)
 */
public record CommitGeneralRequest(
    @PositiveOrZero int supplierId,
    boolean ftz,
    @PositiveOrZero int importerId,
    @NotNull @Size(max = 200) String generalDescription,
    @PositiveOrZero int brandId,
    @PositiveOrZero int brandTypeId,
    @NotNull @Size(max = 50) String model,
    boolean brandImage,
    @NotNull(message = PsMessages.FECHA_REQUERIDA) LocalDate brandImageExpiration,
    @PositiveOrZero int componentPieces,
    @NotNull BigDecimal compensatoryQuota,
    boolean pendingLabel,
    boolean sanitaryNoticeInProcess,
    boolean combined,
    @PositiveOrZero int representativePiece,
    @PositiveOrZero int representativeRange,
    boolean requiresSanitaryNotice
) {
}
