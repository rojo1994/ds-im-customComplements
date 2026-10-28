package com.coppel.dto.response.customcomplements;

import com.coppel.client.customcomplements.dto.TariffsDTO;

/**
 * Fracción MXN capturada en Información aduanal (FrmAduanas.ValidaCampo TXT_FRACCION).
 *
 * @param impiExists la pieza tiene datos de IMPI con la fracción anterior: se pregunta antes de aceptar el cambio
 */
public record FractionCheckDTO(
    FractionRulesDTO rules,
    TariffsDTO tariffs,
    boolean impiExists
) {
}
