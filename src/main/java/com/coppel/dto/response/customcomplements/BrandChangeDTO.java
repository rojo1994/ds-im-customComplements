package com.coppel.dto.response.customcomplements;

/**
 * Cambio de marca (antes ValidaImpi + ObtenerTipoMarca de FrmComplementosAduanales).
 *
 * @param impiExists el código tiene datos de IMPI con la marca anterior: se pregunta antes de aceptar el cambio
 */
public record BrandChangeDTO(
    int brandTypeId,
    String brandType,
    boolean impiExists
) {
}
