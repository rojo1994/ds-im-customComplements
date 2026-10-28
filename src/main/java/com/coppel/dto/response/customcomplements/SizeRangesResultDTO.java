package com.coppel.dto.response.customcomplements;

import com.coppel.dto.customcomplements.SizeRowDTO;

import java.util.List;

/**
 * Rangos de tallas aceptados (FrmRangoDeTallas + RangoTallas): tallas y el grid de componentes reconstruido (un
 * renglón por pieza / rango).
 */
public record SizeRangesResultDTO(
    List<SizeRowDTO> sizes,
    List<ComponentDTO> components
) {
}
