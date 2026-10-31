package com.coppel.client.customcomplements.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Cuerpo de {@code POST /custom-complements-clothing} de ps-tms-fmd-inventarios-centrales (los nombres son los de
 * ese API). El valor UMT sólo va con unidad de tarifa 4. Volts, watts, amperes, hertz, corriente directa y mega amperes
 * no se mandaban: el SP los deja en '0'.
 */
public record ClothingCentralRequest(
    int codigo,
    String fraccion,
    BigDecimal arancelFrontera,
    BigDecimal arancelInterior,
    int unidadComercial,
    boolean arancelPreferencial,
    boolean productor,
    int tipoProductor,
    int criterioPreferencial,
    BigDecimal valorTarifa,
    boolean aplicaDecreto,
    int piezasComponen,
    BigDecimal peso,
    String fraccionUsa,
    int marcaImportacion,
    boolean tieneComponentes,
    BigDecimal precioPermiso,
    int claveNom,
    int cumplimientoNom,
    boolean nomCertificado,
    boolean aplicaNom,
    LocalDate fechaVencimientoNom,
    String avisoSanitario,
    LocalDate fechaVencimientoAviso,
    int nomEtiquetado,
    boolean menos24Volts
) {
}
