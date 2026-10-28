package com.coppel.client.customcomplements.dto;

/**
 * Resultado de dar de alta o cambiar la descripción de una causa.
 *
 * @param duplicated la descripción ya existe en el catálogo (no se grabó)
 */
public record CauseChangeDTO(
    boolean duplicated
) {
}
