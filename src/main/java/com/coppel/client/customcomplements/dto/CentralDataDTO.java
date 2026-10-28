package com.coppel.client.customcomplements.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * proc_mxtrf010obtenerdatoscomplementos: datos que se copian al servidor central. Muebles sólo llena los primeros
 * campos (hasta {@code compensatoryQuota}); Ropa no regresa {@code countryKey}, {@code tmec}, {@code piece} ni
 * {@code compensatoryQuota}.
 */
public record CentralDataDTO(
    String fraction,
    BigDecimal frontierTariff,
    BigDecimal interiorTariff,
    int commercialUnit,
    boolean preferentialTariff,
    boolean producer,
    int producerType,
    int preferentialCriterion,
    int tariffUnit,
    String countryKey,
    boolean tmec,
    int piece,
    BigDecimal compensatoryQuota,
    BigDecimal tariffValue,
    boolean appliesDecree,
    int componentPieces,
    BigDecimal weight,
    String usaFraction,
    String customsDescription,
    int importBrand,
    boolean hasComponents,
    BigDecimal permitPrice,
    boolean requiresPermit,
    boolean appliesNom,
    int nomKey,
    int complianceNom,
    boolean nomCertificate,
    LocalDate nomExpiration,
    String sanitaryNotice,
    LocalDate sanitaryNoticeExpiration,
    int labelingNom,
    boolean under24Volts,
    String volts,
    String watts,
    String amperes,
    String hertz,
    String directCurrent,
    String megaAmperes
) {
}
