package com.coppel.client.customcomplements.dto;

import java.math.BigDecimal;

/**
 * dbo.fun_validapermisoautomatico.
 */
public record AutomaticPermitDTO(
    boolean required,
    BigDecimal percentage,
    BigDecimal price
) {
}
