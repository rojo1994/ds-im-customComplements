package com.coppel.client.customcomplements.dto;

import java.math.BigDecimal;

/**
 * Cuerpo de {@code POST /custom-complements-furniture} de ps-tms-fmd-inventarios-centrales (los nombres son los de
 * ese API). Aranceles y cuota ya van multiplicados por 100, como los mandaba el escritorio.
 */
public record FurnitureCentralRequest(
    int codigo,
    String fraccion,
    BigDecimal arancelFrontera,
    BigDecimal arancelInterior,
    int unidadComercial,
    boolean arancelPreferencial,
    String clavePais,
    boolean tmec,
    boolean productor,
    int tipoProductor,
    boolean criterioPreferencial,
    int cuotaCompensatoria
) {
}
