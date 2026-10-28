package com.coppel.dto.request.customcomplements;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

/**
 * Aceptar de FrmRegulaciones para una pieza / rango.
 *
 * @param profepaException           cmbProfepa
 * @param phytosanitaryCertificate   txbCertificadoFito
 * @param phytosanitaryValidity      dtpFechaVencimiento
 * @param phytosanitaryException     cmbExcepcionCertFito
 * @param complianceInProcess        checkNomTramite (NOM en trámite)
 */
public record RegulationsRequest(
    @PositiveOrZero int piece,
    @PositiveOrZero int sizeRange,
    @NotBlank String fraction,
    Integer profepaException,
    String phytosanitaryCertificate,
    LocalDate phytosanitaryValidity,
    Integer phytosanitaryException,
    boolean complianceInProcess,
    List<@Valid NomInput> complianceNoms,
    List<@Valid NomInput> labelingNoms
) {
    public RegulationsRequest {
        complianceNoms = complianceNoms == null ? List.of() : List.copyOf(complianceNoms);
        labelingNoms = labelingNoms == null ? List.of() : List.copyOf(labelingNoms);
    }
}
