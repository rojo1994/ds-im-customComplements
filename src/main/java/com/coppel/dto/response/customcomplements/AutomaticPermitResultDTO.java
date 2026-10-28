package com.coppel.dto.response.customcomplements;

import java.math.BigDecimal;

/**
 * Permiso automático (fun_validapermisoautomatico) con el color del % (cat_rangoporcentajes).
 */
public record AutomaticPermitResultDTO(
    boolean required,
    BigDecimal percentage,
    BigDecimal price,
    String color
) {
}
