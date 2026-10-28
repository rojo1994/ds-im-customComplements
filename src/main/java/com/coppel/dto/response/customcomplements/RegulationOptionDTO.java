package com.coppel.dto.response.customcomplements;

import com.coppel.client.customcomplements.dto.CatalogItemDTO;

import java.util.List;

/**
 * Sección de FrmRegulaciones: si la fracción la lleva, sus NOM (4 / 5) y sus excepciones.
 */
public record RegulationOptionDTO(
    boolean applies,
    List<CatalogItemDTO> noms,
    List<CatalogItemDTO> exceptions
) {
}
