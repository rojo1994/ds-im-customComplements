package com.coppel.dto.request.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.dto.customcomplements.IepsRowDTO;
import com.coppel.dto.customcomplements.RegulationRowDTO;
import com.coppel.dto.customcomplements.SizeRowDTO;
import com.coppel.dto.customcomplements.SpecificationRowDTO;
import com.coppel.dto.response.customcomplements.ComponentDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * [F10] Guardar con todo lo capturado (Store del front).
 *
 * @param previousBrandId       marca con datos de IMPI que se aceptó cambiar (antes CambioImpi + IdmarcaAnterior)
 * @param impiPieces            piezas con cambio de fracción aceptado
 * @param confirmCostDifference se respondió Sí a "Existe diferencia entre la suma de los costos…"
 */
public record SaveComplementsRequest(
    @NotNull(message = AppMessages.AREA_REQUERIDA) CustomsArea area,
    @Positive(message = AppMessages.CODIGO_6_DIGITOS) int code,
    @Positive(message = AppMessages.EMPLEADO_REQUERIDO) long employee,
    @NotNull @Valid SaveHeaderInput header,
    @NotEmpty List<@Valid ComponentDTO> components,
    List<@Valid CustomsRowDTO> customs,
    List<@Valid SizeRowDTO> sizes,
    List<@Valid RegulationRowDTO> regulations,
    List<@Valid RegulationRowDTO> sanitaryNotice,
    List<@Valid SpecificationRowDTO> specifications,
    List<@Valid IepsRowDTO> ieps,
    Integer previousBrandId,
    List<ImpiPieceInput> impiPieces,
    boolean confirmCostDifference
) {
    public SaveComplementsRequest {
        customs = customs == null ? List.of() : List.copyOf(customs);
        sizes = sizes == null ? List.of() : List.copyOf(sizes);
        regulations = regulations == null ? List.of() : List.copyOf(regulations);
        sanitaryNotice = sanitaryNotice == null ? List.of() : List.copyOf(sanitaryNotice);
        specifications = specifications == null ? List.of() : List.copyOf(specifications);
        ieps = ieps == null ? List.of() : List.copyOf(ieps);
        impiPieces = impiPieces == null ? List.of() : List.copyOf(impiPieces);
    }
}
