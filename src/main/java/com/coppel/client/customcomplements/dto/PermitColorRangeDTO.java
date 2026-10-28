package com.coppel.client.customcomplements.dto;

import java.math.BigDecimal;

/**
 * Rango de Catalogos.dbo.cat_rangoporcentajes con el que proc_mxtrf010obtenerdatosaduanas pinta el % de
 * permiso automático (verde, amarillo, naranja, rojo).
 */
public record PermitColorRangeDTO(
    BigDecimal from,
    BigDecimal to,
    String color
) {
}
