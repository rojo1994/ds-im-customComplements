package com.coppel.client.customcomplements.dto;

import com.coppel.client.customcomplements.dto.PsMessages;
import com.coppel.dto.customcomplements.CustomsRowDTO;
import com.coppel.dto.customcomplements.IepsRowDTO;
import com.coppel.dto.customcomplements.RegulationRowDTO;
import com.coppel.dto.customcomplements.SizeRowDTO;
import com.coppel.dto.customcomplements.SpecificationRowDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Todo lo capturado en la sesión: se escribe en las temporales con los SP proc_mxtrf010actualizartmp* y se
 * confirma con proc_mxtrf010grabarcomplementos 0 y 1 (antes [F10] Guardar).
 */
public record WorkspaceCommitRequest(
    @NotNull(message = PsMessages.DATOS_GENERALES_REQUERIDOS) @Valid CommitGeneralRequest general,
    List<@Valid CustomsRowDTO> customs,
    List<@Valid SizeRowDTO> sizes,
    List<@Valid RegulationRowDTO> regulations,
    List<@Valid SpecificationRowDTO> specifications,
    List<@Valid IepsRowDTO> ieps
) {
    public WorkspaceCommitRequest {
        customs = customs == null ? List.of() : List.copyOf(customs);
        sizes = sizes == null ? List.of() : List.copyOf(sizes);
        regulations = regulations == null ? List.of() : List.copyOf(regulations);
        specifications = specifications == null ? List.of() : List.copyOf(specifications);
        ieps = ieps == null ? List.of() : List.copyOf(ieps);
    }
}
