package com.coppel.dto.response.customcomplements;

import com.coppel.client.customcomplements.dto.CatalogItemDTO;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.dto.customcomplements.IepsRowDTO;
import com.coppel.dto.customcomplements.RegulationRowDTO;
import com.coppel.dto.customcomplements.SizeRowDTO;
import com.coppel.dto.customcomplements.SpecificationRowDTO;

import java.util.List;
import java.util.Map;

/**
 * Todo lo que el escritorio dejaba listo al salir del código (CargarInformacionCodigo): hidrata el Store.
 *
 * @param sanitaryNotice  renglones de la regulación 1 (una por aduana)
 * @param defaultCountry  país del artículo para la primera información aduanal
 * @param warnings        mensajes que no detienen la carga (sin tallas, imagen de marca por vencer / vencida)
 */
public record CodeSessionDTO(
    CodeHeaderDTO header,
    List<InventoryDTO> inventory,
    List<ComponentDTO> components,
    List<CustomsRowDTO> customs,
    Map<String, FractionRulesDTO> fractionRules,
    List<SizeRowDTO> sizes,
    List<RegulationRowDTO> regulations,
    List<RegulationRowDTO> sanitaryNotice,
    List<SpecificationRowDTO> specifications,
    List<IepsRowDTO> ieps,
    CatalogItemDTO defaultCountry,
    ExpirationStatusDTO brandImageStatus,
    boolean requiresSanitaryNotice,
    List<String> warnings
) {
}
