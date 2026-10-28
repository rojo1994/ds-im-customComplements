package com.coppel.client.customcomplements.dto;

/**
 * Respuesta de los SP que sólo regresan {@code existe}.
 */
public record ExistsDTO(
    boolean exists
) {
}
