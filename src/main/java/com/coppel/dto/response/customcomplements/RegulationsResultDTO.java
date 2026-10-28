package com.coppel.dto.response.customcomplements;

import com.coppel.dto.customcomplements.RegulationRowDTO;

import java.util.List;

/**
 * Renglones de regulaciones 2..5 de la pieza / rango (antes LlenarTemporales de FrmRegulaciones).
 */
public record RegulationsResultDTO(
    List<RegulationRowDTO> rows
) {
}
