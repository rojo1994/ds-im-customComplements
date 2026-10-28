package com.coppel.dto.response.customcomplements;

import com.coppel.dto.customcomplements.CustomsRowDTO;

/**
 * Información aduanal aceptada (FrmAduanas.Aceptar): el renglón normalizado que va al Store y las reglas de su
 * fracción.
 */
public record CustomsInfoResultDTO(
    CustomsRowDTO row,
    FractionRulesDTO rules
) {
}
