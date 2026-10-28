package com.coppel.dto.response.customcomplements;

import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.client.customcomplements.dto.IepsUnitDTO;
import com.coppel.client.customcomplements.dto.NonTariffCauseDTO;
import com.coppel.client.customcomplements.dto.PermitColorRangeDTO;

import java.util.List;

/**
 * Combos de la pantalla y de los subformularios, cargados una vez al abrir la opción (antes cada formulario los
 * pedía al abrirse). {@code usaFractionMask} es la máscara fija de txtFraccionUSA; {@code brandTypes} es el combo del
 * alta de marca.
 */
public record ScreenCatalogsDTO(
    List<CatalogItemDTO> importers,
    List<CatalogItemDTO> countries,
    List<CatalogItemDTO> weightUnits,
    List<CatalogItemDTO> commercialUnits,
    List<CatalogItemDTO> quotaTypes,
    List<CatalogItemDTO> producerTypes,
    List<CatalogItemDTO> preferentialCriteria,
    String fractionMask,
    String usaFractionMask,
    List<CatalogItemDTO> customsOffices,
    List<CatalogItemDTO> sanitaryExceptions,
    List<CatalogItemDTO> iepsClasses,
    List<IepsUnitDTO> iepsUnits,
    List<CatalogItemDTO> energyUnits,
    List<PermitColorRangeDTO> permitColorRanges,
    List<NonTariffCauseDTO> nonTariffCauses,
    List<CatalogItemDTO> brandTypes
) {
}
