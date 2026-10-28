package com.coppel.dto.request.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.Locale;

/**
 * Alta de marca (no existía en el escritorio; viene del front anterior). El proveedor no viaja: ds lo toma del código.
 *
 * @param description se guarda en mayúsculas y sin espacios a los lados
 */
public record BrandInput(
    @NotNull(message = AppMessages.AREA_REQUERIDA) CustomsArea area,
    @NotBlank(message = AppMessages.CODIGO_OBLIGATORIO) @Pattern(regexp = "\\d{6}", message = AppMessages.CODIGO_6_DIGITOS) String code,
    @Positive(message = AppMessages.SELECCIONAR_TIPO_MARCA) int brandTypeId,
    @NotBlank(message = AppMessages.CAPTURAR_MARCA)
    @Pattern(regexp = "[ A-Z0-9.,-]{1,50}", message = AppMessages.MARCA_NO_VALIDA) String description
) {
    public BrandInput {
        code = code == null ? null : code.trim();
        description = description == null ? null : description.trim().toUpperCase(Locale.ROOT);
    }

    public int codeNumber() {
        return Integer.parseInt(code);
    }
}
