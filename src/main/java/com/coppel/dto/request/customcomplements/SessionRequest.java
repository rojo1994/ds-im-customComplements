package com.coppel.dto.request.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Código capturado (antes TB_SKU de FrmComplementosAduanales) o código a soltar.
 *
 * @param employee número de empleado de la sesión (antes args[4] de MainCS)
 */
public record SessionRequest(
    @NotNull(message = AppMessages.AREA_REQUERIDA) CustomsArea area,
    @NotBlank(message = AppMessages.CODIGO_OBLIGATORIO) @Pattern(regexp = "\\d{6}", message = AppMessages.CODIGO_6_DIGITOS) String code,
    @Positive(message = AppMessages.EMPLEADO_REQUERIDO) long employee
) {
    public SessionRequest {
        code = code == null ? null : code.trim();
    }

    public int codeNumber() {
        return Integer.parseInt(code);
    }
}
