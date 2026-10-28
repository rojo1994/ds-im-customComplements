package com.coppel.client.customcomplements.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Parámetros de proc_mxtrf010grabarcentralmuebles (MaestroMuebles) o proc_mxtrf010grabarcentralropa (comprasropa).
 * Los valores ya vienen como los mandaba el escritorio (en Muebles los aranceles y la cuota multiplicados por 100;
 * en Ropa el valor UMT sólo con unidad de tarifa 4). Volts, watts, amperes, hertz, corriente directa y mega amperes
 * no se mandaban: el SP los deja en '0'.
 */
public record CentralSaveRequest(
    @NotNull @Size(max = 22) String fraction,
    @NotNull BigDecimal frontierTariff,
    @NotNull BigDecimal interiorTariff,
    int commercialUnit,
    boolean preferentialTariff,
    boolean producer,
    int producerType,
    int preferentialCriterion,
    @Size(max = 3) String countryKey,
    boolean tmec,
    @NotNull BigDecimal compensatoryQuota,
    @NotNull BigDecimal tariffValue,
    boolean appliesDecree,
    int componentPieces,
    @NotNull BigDecimal weight,
    @Size(max = 22) String usaFraction,
    int importBrand,
    boolean hasComponents,
    @NotNull BigDecimal permitPrice,
    int nomKey,
    int complianceNom,
    boolean nomCertificate,
    boolean appliesNom,
    LocalDate nomExpiration,
    String sanitaryNotice,
    LocalDate sanitaryNoticeExpiration,
    int labelingNom,
    boolean under24Volts
) {
}
