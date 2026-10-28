package com.coppel.dto.response.customcomplements;

import com.coppel.dto.customcomplements.RegulationRowDTO;

import java.util.List;

/**
 * Aviso sanitario aceptado: renglones de la regulación 1 y, si aplica, el mensaje de vencidos / por vencer
 * (MostrarMensajeAvisoSanitario).
 */
public record SanitaryNoticeResultDTO(
    List<RegulationRowDTO> rows,
    String message
) {
}
