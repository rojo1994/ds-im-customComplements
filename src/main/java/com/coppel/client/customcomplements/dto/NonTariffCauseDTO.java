package com.coppel.client.customcomplements.dto;

/**
 * Causa para no aplicar arancel preferencial (proc_mxtrf010obtenercausasarancel).
 */
public record NonTariffCauseDTO(
    long id,
    String description,
    boolean active
) {
}
