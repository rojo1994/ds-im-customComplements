package com.coppel.dto.customcomplements;

import jakarta.validation.constraints.Positive;

/**
 * Renglón de {@code tmp_rangotallas<área><código>} (sólo Ropa): talla de una pieza y el rango al que pertenece.
 * Se lee con {@code proc_mxtrf010obtenerdatosrangotallas 1} y se escribe con
 * {@code proc_mxtrf010actualizartmprangotallas}.
 */
public record SizeRowDTO(
    @Positive int piece,
    int size,
    @Positive int range
) {
}
