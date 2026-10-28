package com.coppel.client.customcomplements.dto;

import com.coppel.client.customcomplements.dto.PsMessages;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * dbo.fun_validarfechavencimiento.
 *
 * @param documentId id_Documento de Cat_DocumentosConAlertadeVencimiento (1 aviso sanitario, 2 NOM, 3 imagen de
 *                   marca, 6 certificado fitosanitario)
 */
public record ExpirationCheckRequest(
    @NotNull(message = PsMessages.FECHA_REQUERIDA) LocalDate date,
    @Positive int documentId
) {
}
