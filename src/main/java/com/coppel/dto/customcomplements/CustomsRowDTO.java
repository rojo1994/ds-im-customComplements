package com.coppel.dto.customcomplements;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Renglón de {@code tmp_aduanas<área><código>} (información aduanal de una pieza / rango de tallas). Se lee con
 * {@code proc_mxtrf010obtenerdatosaduanas} y se escribe con {@code proc_mxtrf010actualizartmpaduanas}.
 *
 * <p>Sólo lectura: {@code countryName}, {@code color} (cat_rangoporcentajes) y {@code sanitaryNotice}
 * (la fracción lleva regulación 1). Sólo escritura: {@code weightKg} y {@code tariffUnitId}, que el SP de lectura no
 * regresa (el escritorio los volvía a calcular con validarfraccionmxn y la unidad de peso).
 */
public record CustomsRowDTO(
    @PositiveOrZero int piece,
    @PositiveOrZero int sizeRange,
    @NotBlank @Size(max = 22) String fraction,
    boolean appliesDecree,
    @NotNull @Size(max = 250) String customsDescription,
    @NotNull @Size(max = 250) String composition,
    @NotNull @Size(max = 250) String complement,
    @NotNull @Size(max = 250) String englishDescription,
    int countryId,
    String countryName,
    @NotNull @Size(max = 12) String usaFraction,
    @NotNull BigDecimal unitCost,
    @NotNull BigDecimal permitCost,
    boolean requiresPermit,
    @NotNull BigDecimal permitPercentage,
    int weightUnitId,
    @NotNull BigDecimal weight,
    @Size(max = 25) String weightKg,
    int tariffUnitId,
    int commercialUnitId,
    int componentPieces,
    @NotNull BigDecimal tariffValue,
    @NotNull BigDecimal sugarKg,
    boolean compensatoryQuota,
    int quotaTypeId,
    @NotNull BigDecimal referenceCost,
    boolean specificReferenceCost,
    boolean quotaToPay,
    @NotNull BigDecimal quotaAmount,
    boolean preferentialTariff,
    int nonTariffCauseId,
    boolean tmec,
    boolean producer,
    int producerTypeId,
    int preferentialCriterionId,
    @NotNull BigDecimal normalFrontierTariff,
    @NotNull BigDecimal normalInteriorTariff,
    @NotNull BigDecimal preferentialFrontierTariff,
    @NotNull BigDecimal preferentialInteriorTariff,
    @NotNull BigDecimal applyFrontierTariff,
    @NotNull BigDecimal applyInteriorTariff,
    String color,
    boolean sanitaryNotice
) {
}
