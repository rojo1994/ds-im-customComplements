package com.coppel.client.customcomplements.dto;

import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.dto.customcomplements.IepsRowDTO;
import com.coppel.dto.customcomplements.RegulationRowDTO;
import com.coppel.dto.customcomplements.SizeRowDTO;
import com.coppel.dto.customcomplements.SpecificationRowDTO;

import java.util.List;

/**
 * Lo que el escritorio tenía en las temporales al terminar de cargar el código (CargarInformacionCodigo).
 *
 * @param defaultCountry país del artículo cuando el código aún no tiene información aduanal (el SP regresa sólo
 *                       NumPais / NomPais)
 */
public record WorkspaceSnapshotDTO(
    List<CustomsRowDTO> customs,
    CatalogItemDTO defaultCountry,
    List<SizeRowDTO> sizes,
    List<RegulationRowDTO> regulations,
    List<SpecificationRowDTO> specifications,
    List<IepsRowDTO> ieps
) {
}
