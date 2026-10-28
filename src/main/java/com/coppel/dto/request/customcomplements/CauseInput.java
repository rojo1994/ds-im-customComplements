package com.coppel.dto.request.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * txtCausas de FrmCausasNoAplicarAP.
 */
public record CauseInput(
    @NotBlank(message = AppMessages.CAPTURAR_CAUSA) @Size(max = 200) @Pattern(regexp = "[A-Za-z0-9 ]+", message = AppMessages.CAUSA_NO_VALIDA) String description
) {
}
