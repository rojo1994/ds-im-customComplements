package com.coppel.client.customcomplements.dto;

/**
 * proc_mxtrf010validarfraccionmxn.
 */
public record FractionMxnDTO(
    boolean exists,
    boolean appliesDecree,
    Integer tariffUnitId,
    String tariffUnitName,
    boolean containsSugar
) {
}
