package com.coppel.client.customcomplements.dto;

import com.coppel.client.customcomplements.dto.PsMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Alta o cambio de descripción de una causa (FrmCausasNoAplicarAP, txtCausas).
 */
public record CauseRequest(
    @NotBlank(message = PsMessages.CAUSA_REQUERIDA) @Size(max = 200) String description
) {
}
