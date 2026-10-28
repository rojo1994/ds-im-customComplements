package com.coppel.client.customcomplements.dto;

/**
 * proc_mxtrf010obteneraranceles. Los preferenciales llegan como texto: "N/A" cuando no hay preferencia.
 */
public record TariffsDTO(
    String normalFrontier,
    String normalInterior,
    String preferentialFrontier,
    String preferentialInterior,
    boolean hasPreferential
) {
}
