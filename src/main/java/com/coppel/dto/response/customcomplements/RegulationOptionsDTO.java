package com.coppel.dto.response.customcomplements;

import java.util.List;

/**
 * Secciones de FrmRegulaciones para una fracción (antes ValidarProfepa, ValidarFitosanitario,
 * ValidarNomComplimiento y ValidarNomEtiquetado).
 */
public record RegulationOptionsDTO(
    RegulationOptionDTO profepa,
    RegulationOptionDTO phytosanitary,
    RegulationOptionDTO complianceNoms,
    RegulationOptionDTO labelingNoms,
    List<String> warnings
) {
}
